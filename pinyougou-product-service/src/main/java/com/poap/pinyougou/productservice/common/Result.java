package com.poap.pinyougou.productservice.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder // Lombok的Builder注解，为我们实现了建造者模式
public class Result<T> {

    private int code;      // 响应码，例如 200代表成功，500代表失败
    private String message;  // 响应消息
    private T data;        // 响应数据

    // 成功的静态工厂方法
    public static <T> Result<T> success(T data) {
        return Result.<T>builder()
                .code(200)
                .message("Success")
                .data(data)
                .build();
    }
    
    // 成功的静态工厂方法 (无数据)
    public static <T> Result<T> success() {
        return success(null);
    }

    // 失败的静态工厂方法
    public static <T> Result<T> fail(int code, String message) {
        return Result.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .build();
    }
}