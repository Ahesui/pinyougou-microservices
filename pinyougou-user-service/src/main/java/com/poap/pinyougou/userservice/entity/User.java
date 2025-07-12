package com.poap.pinyougou.userservice.entity;

import lombok.Data; // Lombok注解，自动生成getter/setter等
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;

    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "updated_time")
    private LocalDateTime updatedTime;

    @PrePersist // 在保存前自动设置创建时间
    public void prePersist() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
    }

    @PreUpdate // 在更新前自动设置更新时间
    public void preUpdate() {
        this.updatedTime = LocalDateTime.now();
    }
}