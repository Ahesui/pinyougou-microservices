package com.poap.pinyougou.userservice.controller.dto;

import lombok.Data;
import com.poap.pinyougou.userservice.service.login.LoginType; // 导入LoginType


@Data
public class LoginRequest {
    private String username;
    private String password;
    private LoginType loginType; // 添加登录类型字段
}