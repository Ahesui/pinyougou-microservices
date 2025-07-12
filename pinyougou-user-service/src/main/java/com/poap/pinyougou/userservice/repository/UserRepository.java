package com.poap.pinyougou.userservice.repository;


import com.poap.pinyougou.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional; // 使用Optional避免空指针

public interface UserRepository extends JpaRepository<User, Long> {
    // Spring Data JPA会根据方法名自动生成SQL查询
    Optional<User> findByUsername(String username);
}
