package com.poap.pinyougou.userservice.service;

import com.poap.pinyougou.userservice.controller.dto.LoginRequest;
import com.poap.pinyougou.userservice.entity.User;
import com.poap.pinyougou.userservice.repository.UserRepository;
import com.poap.pinyougou.userservice.service.login.LoginStrategy;
import com.poap.pinyougou.userservice.service.login.LoginStrategyFactory;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder; // 导入加密器

@Service
@Slf4j
public class UserService {

    @Autowired
    private LoginStrategyFactory loginStrategyFactory; // 注入策略工厂

    @Autowired
    private NotificationService notificationService; // 注入通知服务

    @Autowired
    private PasswordEncoder passwordEncoder; // 注入加密器

    @Autowired
    private UserRepository userRepository;
    // /**
    //  * 用户注册方法
    //  * @param user 用户信息
    //  * @return 注册后的用户信息
    //  */
    // public User register(User user) {
    //     // 在实际项目中，这里需要检查用户名是否已存在，密码需要加密等
    //     // 在这一步，我们先简化处理
    //     return userRepository.save(user);
    // }

    public User register(User user) {
        // 1. 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在"); // 抛出异常，会被全局异常处理器捕获
        }

        // 2. 对密码进行加密
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        User savedUser = userRepository.save(user);
        // 调用异步方法
        notificationService.sendWelcomeMessage(savedUser);

        log.info("用户 {} 注册方法执行完毕，主线程返回。", savedUser.getUsername());
        // 3. 保存用户
        return savedUser;
    }


    public User login(LoginRequest loginRequest) {//重构：替换原先的参数 String username, String rawPassword
        // // 1. 根据用户名查找用户
        // User user = userRepository.findByUsername(username)
        //         .orElseThrow(() -> new RuntimeException("用户不存在"));

        // // 2. 匹配密码
        // // passwordEncoder.matches会比较明文密码和加密后的密码是否匹配
        // if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
        //     throw new RuntimeException("密码错误");
        // }

        // 1. 从工厂获取对应的策略
        LoginStrategy strategy = loginStrategyFactory.getStrategy(loginRequest.getLoginType());
        
        // 2. 执行策略
        User user = strategy.login(loginRequest);
        
        // 为了安全，登录成功后返回的用户信息不应包含密码
        user.setPassword(null); 
        return user;
    }

}