package com.poap.pinyougou.productservice.repository;

import com.poap.pinyougou.productservice.entity.Product;



import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // // 未来可以添加基于分类和品牌的查询方法
    //     // 当调用这个方法时，JPA会在事务中发出 SELECT ... FOR UPDATE 语句
    // @Lock(LockModeType.PESSIMISTIC_WRITE)
    // Optional<Product> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(Long id);
}