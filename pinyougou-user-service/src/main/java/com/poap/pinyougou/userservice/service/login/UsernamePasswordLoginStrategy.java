package com.poap.pinyougou.userservice.service.login;

import com.poap.pinyougou.userservice.controller.dto.LoginRequest;
import com.poap.pinyougou.userservice.entity.User;
import com.poap.pinyougou.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor; // 使用Lombok的构造器注入
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor // Lombok注解，为final字段生成构造函数，等同于@Autowired
public class UsernamePasswordLoginStrategy implements LoginStrategy {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginType supports() {
        return LoginType.USERNAME_PASSWORD;
    }

    @Override
    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }
}