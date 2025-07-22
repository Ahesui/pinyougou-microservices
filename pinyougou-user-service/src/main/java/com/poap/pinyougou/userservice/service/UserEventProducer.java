package com.poap.pinyougou.userservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poap.pinyougou.userservice.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;
import org.apache.rocketmq.client.producer.SendResult;
import org.springframework.context.annotation.Import;
import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;

/**
 * 专门负责发送用户相关领域事件消息的生产者
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Import(RocketMQAutoConfiguration.class) 
public class UserEventProducer {

    private final RocketMQTemplate rocketMQTemplate;
    private final ObjectMapper objectMapper;

    private static final String USER_REGISTRATION_TOPIC = "USER_REGISTRATION_TOPIC";

    /**
     * 发送用户注册成功的消息
     * @param user 已注册并保存到数据库的用户对象
     */
    public void sendUserRegisteredMessage(User user) {
        // 1. 准备消息内容
        String messagePayload;
        try {
            // 创建一个副本或DTO来发送，避免修改原始对象
            User userToSend = new User();
            userToSend.setId(user.getId());
            userToSend.setUsername(user.getUsername());
            userToSend.setEmail(user.getEmail());
            // ... 只包含需要发送的字段

            messagePayload = objectMapper.writeValueAsString(userToSend);
        } catch (JsonProcessingException e) {
            log.error("序列化User对象失败, user: {}", user, e);
            // 序列化失败，消息无法发送，直接返回
            return;
        }

        // 2. 发送消息
        try {
            rocketMQTemplate.convertAndSend(USER_REGISTRATION_TOPIC, messagePayload);
            log.info("成功发送用户注册消息到MQ. Topic: {}, Message: {}", USER_REGISTRATION_TOPIC, messagePayload);
        } catch (Exception e) {
            // 3. 处理发送失败的异常
            // 在生产环境中，这里应该有更健壮的补偿机制，
            // 例如：将失败的消息存入数据库的"待重发消息表"，由定时任务进行重试。
            // 当前阶段，我们先详细记录日志。
            log.error("发送用户注册消息到MQ失败. Topic: {}, Message: {}", USER_REGISTRATION_TOPIC, messagePayload, e);
        }
    }

    /**
     * 【异步】发送用户注册成功的消息
     * @param user 已注册并保存到数据库的用户对象
     */
    public void sendUserRegisteredMessageAsync(User user) {
        String messagePayload;
        try {
            User userToSend = new User();
            userToSend.setId(user.getId());
            userToSend.setUsername(user.getUsername());
            userToSend.setEmail(user.getEmail());
            messagePayload = objectMapper.writeValueAsString(userToSend);
        } catch (JsonProcessingException e) {
            log.error("序列化User对象失败, user: {}", user, e);
            return;
        }

        // 使用 asyncSend 方法
        rocketMQTemplate.asyncSend(USER_REGISTRATION_TOPIC, messagePayload, new SendCallback() {
            
            // 发送成功后，RocketMQ会回调这个方法
            @Override
            public void onSuccess(SendResult sendResult) {
                log.info("异步发送用户注册消息成功. Topic: {}, MsgId: {}", USER_REGISTRATION_TOPIC, sendResult.getMsgId());
            }

            // 发送失败后，RocketMQ会回调这个方法
            @Override
            public void onException(Throwable e) {
                log.error("异步发送用户注册消息失败. Topic: {}, Message: {}", USER_REGISTRATION_TOPIC, messagePayload, e);
                // 同样，这里需要补偿机制
            }
        });

        // 调用asyncSend后，这里的日志会立即打印，不会等待发送结果
        log.info("已提交异步发送任务，主线程继续执行...");
    }

    // 未来可以添加更多方法
    // public void sendUserProfileUpdatedMessage(User user) { ... }
}