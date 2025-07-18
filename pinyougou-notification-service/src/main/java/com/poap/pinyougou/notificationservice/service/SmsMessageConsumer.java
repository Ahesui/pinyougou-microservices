package com.poap.pinyougou.notificationservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

// topic: 指定要消费的主题
// consumerGroup: 定义消费者组，不同组可以重复消费，同一组内只有一个消费者能消费
@Slf4j
@Service
@RocketMQMessageListener(topic = "USER_REGISTRATION_TOPIC", consumerGroup = "notification-group")
public class SmsMessageConsumer implements RocketMQListener<String> {

    // 当Topic中有新消息时，这个方法就会被调用
    @Override
    public void onMessage(String message) {
        log.info("收到用户注册消息，准备发送欢迎短信。消息内容: {}", message);
        
        // 模拟发送短信的业务逻辑
        try {
            // 这里 message 可能是用户的JSON字符串，可以反序列化后获取手机号等信息
            Thread.sleep(2000); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("欢迎短信发送完成！");
    }
}