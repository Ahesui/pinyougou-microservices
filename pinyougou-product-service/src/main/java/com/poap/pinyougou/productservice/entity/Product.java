package com.poap.pinyougou.productservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String productName;
    private String category;
    private String brand;
    private BigDecimal price;
    private Integer stock;
    @Column(name = "created_time")
    private LocalDateTime createdTime;
    @Version
    private Integer version;
}