// package com.poap.pinyougou.userservice.debug;
// package com.poap.pinyougou.notificationservice.debug;

// import org.apache.rocketmq.spring.core.RocketMQTemplate;
// import org.apache.rocketmq.client.producer.DefaultMQProducer;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.env.Environment;

// @Configuration
// public class Rconfig {
//     @Autowired
//     private Environment environment;

//     @Bean
//     public RocketMQTemplate rocketMQTemplate() {
//         RocketMQTemplate template = new RocketMQTemplate();

//         // 手动创建 Producer 但不要调用 start()
//         DefaultMQProducer producer = new DefaultMQProducer(environment.getProperty("rocketmq.producer.group"));
//         producer.setNamesrvAddr(environment.getProperty("rocketmq.name-server"));

//         template.setProducer(producer);
//         return template;
//     }
// }
