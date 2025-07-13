package com.poap.pinyougou.productservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import com.poap.pinyougou.productservice.entity.Product;

@Data
@NoArgsConstructor
public class ProductDetailDTO {
    /**
     * 商品的基本信息。
     * 这里可以直接复用Product实体类，因为它包含了所有商品自身的信息。
     * 如果Product实体类中有很多前端不需要的敏感或冗余字段，
     * 更好的做法是再创建一个ProductInfoDTO，只包含需要的字段。
     * 但在当前阶段，直接复用是OK的。
     */
    private Product product;

    /**
     * 商品发布者的用户名。
     * 这个信息是从用户服务(user-service)远程调用获取的。
     */
    private String publisherName;

    /**
     * 商品发布者的ID。
     * 方便前端可能需要的跳转或其他操作。
     */
    private Long publisherId;

    /**
     * 一个方便的构造函数，用于快速创建对象
     * @param product 商品实体
     * @param publisherName 发布者名称
     * @param publisherId 发布者ID
     */
    public ProductDetailDTO(Product product, String publisherName, Long publisherId) {
        this.product = product;
        this.publisherName = publisherName;
        this.publisherId = publisherId;
    }

    
}
