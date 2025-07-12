package com.poap.pinyougou.userservice.service;

import com.poap.pinyougou.userservice.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    @Async // 将此方法标记为异步方法
    public void sendWelcomeMessage(User user) {
        log.info("开始异步发送欢迎邮件给: {}", user.getUsername());
        try {
            // 模拟耗时的邮件发送操作
            Thread.sleep(3000); // 暂停3秒
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("发送欢迎邮件时线程被中断", e);
        }
        log.info("欢迎邮件发送完成！收件人: {}", user.getUsername());
    }
}