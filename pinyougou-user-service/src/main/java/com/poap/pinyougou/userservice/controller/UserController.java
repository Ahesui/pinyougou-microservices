package com.poap.pinyougou.userservice.controller;

import com.poap.pinyougou.userservice.entity.User;
import com.poap.pinyougou.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.poap.pinyougou.userservice.common.Result;
import com.poap.pinyougou.userservice.controller.dto.LoginRequest;
import com.poap.pinyougou.userservice.repository.UserRepository;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public Result<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.register(user);
        // 为了安全，不将密码返回给前端
        registeredUser.setPassword(null);
        return Result.success(registeredUser);
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody LoginRequest loginRequest) {
        User loggedInUser = userService.login(loginRequest);
        return Result.success(loggedInUser);
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        user.setPassword(null); // 永远不要返回密码
        return Result.success(user);
    }
}