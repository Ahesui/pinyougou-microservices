// package com.poap.pinyougou.userservice.debug;
// import lombok.extern.slf4j.Slf4j;
// import org.apache.rocketmq.spring.core.RocketMQTemplate;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.NoSuchBeanDefinitionException;
// import org.springframework.boot.autoconfigure.condition.ConditionEvaluationReport;
// import org.springframework.boot.context.event.ApplicationFailedEvent;
// import org.springframework.boot.context.event.ApplicationReadyEvent;
// import org.springframework.context.ApplicationContext;
// import org.springframework.context.event.EventListener;
// import org.springframework.core.env.Environment;
// import org.springframework.stereotype.Component;
// import org.springframework.context.annotation.Import;
// import org.apache.rocketmq.spring.autoconfigure.RocketMQAutoConfiguration;

// import java.util.Map;

// @Slf4j
// @Component
// @Import(RocketMQAutoConfiguration.class) 
// public class RTest {

//     @Autowired
//     private Environment environment;

//     @Autowired
//     private ApplicationContext applicationContext;

//     @Autowired(required = false)
//     private RocketMQTemplate rocketMQTemplate;

//     @EventListener(ApplicationReadyEvent.class)
//     public void onReady() {
//         log.info("========== [RocketMQ 启动诊断报告] ==========");
//         log.info("rocketmq.name-server: {}", environment.getProperty("rocketmq.name-server"));
//         log.info("rocketmq.producer.group: {}", environment.getProperty("rocketmq.producer.group"));
//         log.info("spring.application.name: {}", environment.getProperty("spring.application.name"));
//         log.info("spring.profiles.active: {}", environment.getProperty("spring.profiles.active"));
//         log.info("rocketmq.enabled: {}", environment.getProperty("rocketmq.enabled", "true"));

//         if (rocketMQTemplate != null) {
//             log.info("✅ RocketMQTemplate 已通过 @Autowired 注入！");
//         } else {
//             log.error("❌ RocketMQTemplate 注入失败！");
//         }

//         try {
//             RocketMQTemplate template = applicationContext.getBean(RocketMQTemplate.class);
//             log.info("✅ RocketMQTemplate 可通过 ApplicationContext 获取！");
//         } catch (NoSuchBeanDefinitionException ex) {
//             log.error("❌ ApplicationContext 中无 RocketMQTemplate Bean！");
//         }

//         ConditionEvaluationReport report = applicationContext.getBean(ConditionEvaluationReport.class);
//         Map<String, ConditionEvaluationReport.ConditionAndOutcomes> conditions = report.getConditionAndOutcomesBySource();
//         conditions.forEach((key, outcome) -> {
//             if (key.toLowerCase().contains("rocketmq")) {
//                 log.info("[AutoConfig========] {}: {}", key, outcome);
//             }
//         });


//         log.info("[AutoConfig] RocketMQ 相关自动配置结果如下：");
//         log.info("[AutoConfig] RocketMQ 相关自动配置结果如下：");

//         report.getConditionAndOutcomesBySource().forEach((source, outcomes) -> {
//             if (source.toLowerCase().contains("rocketmq")) {
//                 log.info("[AutoConfig Source] {}", source);
//                 for (ConditionEvaluationReport.ConditionAndOutcome outcome : outcomes) {
//                     String conditionClass = outcome.getCondition().getClass().getName();
//                     String message = outcome.getOutcome().getMessage();
//                     // 老版本中没有 isMatch()，我们手动根据 outcome.getOutcome().isMatch() 来推断
//                     boolean matched = message == null || !message.contains("did not match");
//                     log.info("  -> 条件类: {}", conditionClass);
//                     log.info("     可能匹配: {}", matched ? "是 ✅" : "否 ❌");
//                     log.info("     原因: {}", message);
//                 }
//             }
//         });

//         log.info("========================================");
//     }

//     @EventListener
//     public void handleStartupFailed(ApplicationFailedEvent event) {
//         Throwable ex = event.getException();
//         log.error("❌ Spring Boot 启动失败，分析异常原因如下：");
//         printExceptionRecursive(ex, 0);
//     }

//     private void printExceptionRecursive(Throwable ex, int level) {
//         if (ex == null || level > 10) return;
//         log.error("{}原因：{}", "  ".repeat(level), ex.getMessage(), ex);
//         printExceptionRecursive(ex.getCause(), level + 1);
//     }
// }