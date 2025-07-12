package com.poap.pinyougou.userservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync // 核心注解，开启Spring的异步方法执行功能
public class AsyncConfig {
    // 未来可以在这里配置自定义的线程池，但现在保持简单即可
}