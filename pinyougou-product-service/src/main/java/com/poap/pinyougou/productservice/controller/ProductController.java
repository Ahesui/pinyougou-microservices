package com.poap.pinyougou.productservice.controller;

import com.poap.pinyougou.productservice.common.Result;
import com.poap.pinyougou.productservice.dto.ProductDetailDTO;
import com.poap.pinyougou.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import com.poap.pinyougou.productservice.entity.Product;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/{productId}/seckill-v0")
    public Result<String> seckillProductV0(@PathVariable Long productId) {
        boolean success = productService.seckillV0(productId);
        return success ? Result.success("秒杀成功") : Result.fail(500, "秒杀失败，库存不足");
    }

    @PostMapping("/{productId}/seckill-v1")
    public Result<String> seckillProductV1(@PathVariable Long productId) {
        boolean success = productService.seckillV1(productId);
        return success ? Result.success("秒杀成功") : Result.fail(500, "秒杀失败，库存不足");
    }

    // 在 ProductController.java 中
    @PostMapping("/{productId}/seckill-v2")
    public Result<String> seckillProductV2(@PathVariable Long productId) {
        boolean success = productService.seckillV2(productId);
        return success ? Result.success("秒杀成功") : Result.fail(500, "秒杀失败，库存不足");
    }

    @PostMapping("/{productId}/seckill-v3")
    public Result<String> seckillProductV3(@PathVariable Long productId) {
        boolean success = productService.seckillV3(productId);
        return success ? Result.success("秒杀成功") : Result.fail(500, "秒杀失败，库存不足");
    }

    @PostMapping("/{productId}/seckill-v4")
    public Result<String> seckillProductV4(@PathVariable Long productId) {
        boolean success = productService.seckillV4(productId);
        return success ? Result.success("秒杀成功") : Result.fail(500, "秒杀失败，库存不足");
    }

    @GetMapping("/{id}/detail")
    public Result<ProductDetailDTO> getProductDetail(@PathVariable Long id) {
        ProductDetailDTO productDetail = productService.getProductDetail(id);
        return Result.success(productDetail);
    }

    /**
     * 修改后的接口：分页获取所有商品列表
     * 它可以自动接收URL参数，如: /api/products?page=0&size=10&sort=id,desc
     * 
     * @param pageable Spring MVC会自动将URL参数注入到这个对象中
     * @return 包含分页信息的统一响应体
     */
    @GetMapping
    public Result<Page<Product>> getAllProductsPaged(
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        Page<Product> productPage = productService.findProductsPaged(pageable);
        return Result.success(productPage);
    }
}