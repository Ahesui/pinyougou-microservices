package com.poap.pinyougou.userservice.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice // 声明这是一个全局异常处理组件
public class GlobalExceptionHandler {

    // 捕获所有RuntimeException及其子类
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException: ", e); // 在服务器日志中打印详细错误
        // 返回给前端一个统一的、不暴露内部细节的错误信息
        return Result.fail(500, "服务器内部错误，请联系管理员");
    }

    // 你还可以定义更多针对特定异常的处理方法
    // 例如：@ExceptionHandler(BusinessException.class)
}