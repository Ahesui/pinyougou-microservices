package com.poap.pinyougou.productservice.service;

import com.poap.pinyougou.productservice.entity.Product;
import com.poap.pinyougou.productservice.feign.UserDTO;
import com.poap.pinyougou.productservice.feign.UserFeignClient;
import com.poap.pinyougou.productservice.repository.ProductRepository;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.Query;
import com.poap.pinyougou.productservice.common.Result;
import com.poap.pinyougou.productservice.dto.ProductDetailDTO;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserFeignClient userFeignClient; // 注入Feign客户端
    private final RedissonClient redissonClient; // 注入Redisson客户端

    @Autowired
    private EntityManager entityManager;

    // 假设有一个获取商品详情的方法
    public ProductDetailDTO getProductDetail(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在 id：" + productId));

        // 2. 远程调用用户服务获取发布者信息
        // 假设我们的Product实体中有一个字段叫 publisherUserId
        // Long publisherUserId = product.getPublisherUserId();
        // 在我们当前的表设计中没有这个字段，我们先硬编码一个ID来测试
        Long publisherUserId = 1L;
        String finalPublisherName = "未知发布者";
        Long finalPublisherId = null;

        try {
            Result<UserDTO> userResult = userFeignClient.getUserById(publisherUserId);
            // 检查调用是否成功，并且返回了数据
            if (userResult != null && userResult.getCode() == 200 && userResult.getData() != null) {
                finalPublisherName = userResult.getData().getUsername();
                finalPublisherId = userResult.getData().getId();
            } else {
                log.warn("调用用户服务失败或未找到用户, userId: {}. Response: {}", publisherUserId, userResult);
            }
        } catch (Exception e) {
            log.error("远程调用用户服务异常, userId: {}", publisherUserId, e);
            // 发生异常，比如服务不可用，可以设置默认值或进行特定处理
        }

        // 3. 组装DTO并返回
        return new ProductDetailDTO(product, finalPublisherName, finalPublisherId);
    }

    // 在 ProductService.java 中
    @Transactional
    public boolean seckillV0(Long productId) {
        // 1. 先用SQL查出库存 (绕过JPA缓存)
        Query query = entityManager.createNativeQuery("SELECT stock FROM t_product WHERE id = :id");
        query.setParameter("id", productId);
        Integer stock = (Integer) query.getSingleResult();

        if (stock > 0) {
            log.info("线程 {}: V0 当前库存--{} > 0，准备扣减库存", Thread.currentThread().getName(), stock);

            // 2. 模拟耗时，放大并发问题
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }

            // 3. 直接用SQL更新 (绕过JPA的save)
            Query updateQuery = entityManager
                    .createNativeQuery("UPDATE t_product SET stock = stock - 1 WHERE id = :id");
            updateQuery.setParameter("id", productId);
            int updatedRows = updateQuery.executeUpdate();

            log.info("query:{}", updateQuery.unwrap(org.hibernate.query.NativeQuery.class).getQueryString());
            if (updatedRows > 0) {
                log.info("线程 {}: V0 库存扣减成功", Thread.currentThread().getName());
                return true;
            }
        }

        log.info("线程 {}: V0 库存不足或更新失败", Thread.currentThread().getName());
        return false;
    }

    // 版本一：无任何锁的秒杀逻辑
    @Transactional
    public boolean seckillV1(Long productId) {
        // 1. 查询库存
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));
        int stock = product.getStock();
        if (stock > 0) {
            log.info("线程 {}: 当前库存--：{} > 0，准备扣减库存", Thread.currentThread().getName(), stock);
            // 2. 扣减库存
            int newStock = stock - 1;
            product.setStock(newStock);
            // 3. 更新数据库
            productRepository.save(product);
            log.info("线程 {}: 库存扣减成功，新库存数为--：{}", Thread.currentThread().getName(), newStock);
            return true;
        } else {
            log.info("线程 {}: 库存不足，当前库存数为--：{}", Thread.currentThread().getName(), stock);
            return false;
        }
    }

    // 在 ProductService.java 中
    @Transactional
    public boolean seckillV2(Long productId) {
        // 这个findById现在是带悲观锁的了！
        Product product = productRepository.findByIdForUpdate(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (product.getStock() > 0) {
            log.info("线程 {}: (V2-悲观锁) 获取锁成功，准备扣减库存", Thread.currentThread().getName());
            // 模拟一些业务耗时
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
            }

            product.setStock(product.getStock() - 1);
            productRepository.save(product);
            log.info("线程 {}: (V2-悲观锁S) 库存扣减成功", Thread.currentThread().getName());
            return true;
        } else {
            log.info("线程 {}: (V2-悲观锁F) 库存不足", Thread.currentThread().getName());
            return false;
        }
    }

    // 在 ProductService.java 中
    // 注意，这里不需要@Transactional了，因为我们想手动控制重试
    public boolean seckillV3(Long productId) {
        int maxRetries = 3; // 最大重试次数
        int retryCount = 0;
        while (retryCount < maxRetries) {
            try {
                Product product = productRepository.findById(productId).get();
                if (product.getStock() > 0) {
                    // 模拟一些业务耗时
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                    }
                    product.setStock(product.getStock() - 1);
                    productRepository.save(product); // JPA 在这里会执行 UPDATE ... WHERE id=? AND version=?
                    log.error("线程 {}: (V3-乐观锁S-) 更新成功，重试次数：{}", Thread.currentThread().getName(), retryCount);
                    return true;
                }
                log.error("线程 {}: (V3-乐观锁F-1) 更新成功，重试次数：{}", Thread.currentThread().getName(), retryCount);
                return false;
            } catch (Exception e) {
                log.error("线程 {}: (V3-乐观锁F-2) 更新失败，发生冲突，重试次数：{}", Thread.currentThread().getName(), retryCount);
                retryCount++;
            }
        }
        return false;
    }

    // 版本四：基于Redisson的分布式锁
    public boolean seckillV4(Long productId) {
        final String lockKey = "lock:product:" + productId;
        RLock lock = redissonClient.getLock(lockKey);

        try {
            // 1. 尝试获取锁，最多等待10秒，获取后30秒自动释放
            boolean isLocked = lock.tryLock(10, 30, TimeUnit.SECONDS);

            if (isLocked) {
                log.info("线程 {}: 获取到Redisson分布式锁 {}", Thread.currentThread().getName(), lockKey);
                // 业务逻辑 (和之前的V1版本一样)
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null && product.getStock() > 0) {
                    product.setStock(product.getStock() - 1);
                    productRepository.save(product);
                    log.info("库存扣减成功");
                    return true;
                }
                return false;
            } else {
                log.info("线程 {}: 未能获取到分布式锁 {}", Thread.currentThread().getName(), lockKey);
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            // 2. 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                log.info("线程 {}: 释放锁 {}", Thread.currentThread().getName(), lockKey);
            }
        }
    }

    /**
     * 新增方法：分页查询商品列表
     * 
     * @param pageable 封装了分页和排序信息的对象
     * @return 分页后的商品数据
     */
    public Page<Product> findProductsPaged(Pageable pageable) {
        log.info("正在分页查询商品信息: page={}, size={}, sort={}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        // 调用JPA内置的分页查询方法
        return productRepository.findAll(pageable);
    }
}