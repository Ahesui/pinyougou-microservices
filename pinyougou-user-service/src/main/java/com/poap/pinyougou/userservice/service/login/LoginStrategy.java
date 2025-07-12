package com.poap.pinyougou.userservice.service.login;
import com.poap.pinyougou.userservice.entity.User;
import com.poap.pinyougou.userservice.controller.dto.LoginRequest;

public interface LoginStrategy {
    // 支持的登录类型
    LoginType supports();
    // 登录逻辑
    User login(LoginRequest request);
}