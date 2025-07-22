package com.poap.pinyougou.userservice;


import org.apache.rocketmq.client.impl.factory.MQClientInstance;
import org.apache.rocketmq.client.impl.producer.DefaultMQProducerImpl;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.lang.reflect.Field; 

@SpringBootTest
class PinyougouUserServiceApplicationTests {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * RocketMQ 连接与发送诊断测试
     */
    @Test
    void diagnoseRocketMQConnection() throws Exception{
        System.out.println("========= 开始RocketMQ诊断 =========");

        System.out.println("========= 开始RocketMQ诊断 =========");
        String testTopic = "USER2_REGISTRATION_TOPIC";

        // ... (省略前面的检查) ...

        System.out.println("\n========= 新增步骤：主动获取Topic路由信息 =========");
        System.out.println("准备查询Topic [" + testTopic + "] 的路由...");

        // 获取底层Producer实例
        DefaultMQProducer producer = rocketMQTemplate.getProducer();
        Object unwrappedProducer = AopProxyUtils.getSingletonTarget(producer);
        if (!(unwrappedProducer instanceof DefaultMQProducerImpl)) {
            System.err.println("错误: 未能获取到底层的DefaultMQProducerImpl实例。");
            return;
        }
        DefaultMQProducerImpl producerImpl = (DefaultMQProducerImpl) unwrappedProducer;

        // 手动触发一次路由信息获取，以确保缓存被填充
        List<MessageQueue> queues = producerImpl.fetchPublishMessageQueues(testTopic);
        if (queues == null || queues.isEmpty()) {
            System.err.println("错误: 未能从NameServer获取到Topic [" + testTopic + "] 的任何路由信息！");
            return;
        }

        // ======== 解决方案：通过反射获取Broker地址缓存 ========
        // 0. 获取 DefaultMQProducerImpl 内部的 MQClientInstance
        Field clientInstanceField = DefaultMQProducerImpl.class.getDeclaredField("mqClientFactory");
        clientInstanceField.setAccessible(true); // 允许访问私有字段
        MQClientInstance clientInstance = (MQClientInstance) clientInstanceField.get(producerImpl);
        if (clientInstance == null) {
            System.err.println("错误: 无法获取到 MQClientInstance。");
            return;
        }

        // 4. 从 MQClientInstance 中获取 brokerAddrTable (Broker地址表)
        Field brokerAddrTableField = MQClientInstance.class.getDeclaredField("brokerAddrTable");
        brokerAddrTableField.setAccessible(true);
        ConcurrentHashMap<String, String> brokerAddrTable = 
            (ConcurrentHashMap<String, String>) brokerAddrTableField.get(clientInstance);

        System.out.println("\n成功获取到生产者内部的Broker地址缓存表: " + brokerAddrTable);
        // ======== 反射获取结束 ========
        
        System.out.println("\n成功从NameServer获取到Topic [" + testTopic + "] 的路由信息！详情如下:");
        System.out.println("--------------------------------------------------");
        for (int i = 0; i < queues.size(); i++) {
            MessageQueue queue = queues.get(i);
            String brokerName = queue.getBrokerName();
            String brokerAddr = brokerAddrTable.get(brokerName); // 5. 从缓存表中查找地址

            System.out.printf("  路由 %d:\n", i + 1);
            System.out.println("    - Broker Name: " + brokerName);
            System.out.println("    - Topic      : " + queue.getTopic());
            System.out.println("    - Queue ID   : " + queue.getQueueId());
            System.out.println("    - Broker Addr: " + (brokerAddr != null ? brokerAddr : "未在缓存中找到"));
            System.out.println("--------------------------------------------------");
        }

        System.out.println("\n========= 诊断：路由信息获取完毕 =========");
        String nameServerAddr = producer.getNamesrvAddr();
		 

        // 步骤 2: 尝试解析NameServer的主机名
        try {
            String host = nameServerAddr.split(":")[0];
            System.out.println("2. 尝试解析主机名: " + host);
            InetAddress.getByName(host);
            System.out.println("   -> 主机名解析成功！");
        } catch (UnknownHostException e) {
            System.err.println("错误: 无法解析NameServer的主机名！请检查地址是否正确，或者DNS/hosts文件是否有问题。");
            e.printStackTrace();
            return;
        } catch (Exception e) {
            System.err.println("错误: 解析NameServer地址时发生未知错误。地址: " + nameServerAddr);
            e.printStackTrace();
            return;
        }

        // 步骤 3: 发送一条测试消息 (使用同步发送，以便立即看到结果)
        String testMessage = "这是一条来自诊断测试的消息 at " + System.currentTimeMillis();
        System.out.println("3. 尝试向Topic [" + testTopic + "] 同步发送一条消息...");
        System.out.println("   消息内容: " + testMessage);

		 

		
        try {
            // 设置一个更长的超时时间进行测试，比如10秒
            SendResult sendResult = rocketMQTemplate.syncSend(testTopic, testMessage, 10000);
            
            System.out.println("   -> 消息发送成功！");
            System.out.println("   -> SendResult: " + sendResult);
            System.out.println("   -> 消息ID: " + sendResult.getMsgId());
            System.out.println("   -> 状态: " + sendResult.getSendStatus());
        } catch (Exception e) {
            System.err.println("错误: 发送消息时发生异常！这是问题的关键所在。");
            System.err.println("异常类型: " + e.getClass().getName());
            System.err.println("异常信息: " + e.getMessage());
            e.printStackTrace();
            System.err.println("\n可能原因排查:");
            System.err.println("  a) RocketMQ Broker是否正常运行？(使用jps或mqadmin检查)");
            System.err.println("  b) 应用服务器与Broker之间的网络是否通畅？(检查防火墙，尝试ping Broker IP)");
            System.err.println("  c) NameServer返回的Broker地址，你的应用是否能访问？(常见于Docker环境)");
            System.err.println("  d) 客户端与服务端版本是否兼容？");
            return;
        }

        System.out.println("========= RocketMQ诊断结束: 看起来一切正常！ =========");
    }



	@Test
    void diagnoseRocketMQRoute() {
        System.out.println("========= 开始RocketMQ路由独立诊断 =========");

        String nameServerAddr = "localhost:9876"; // 直接硬编码你的NameServer地址
        String testTopic = "USER2_REGISTRATION_TOPIC";
        String producerGroup = "diagnose-producer-group"; // 定义一个临时的生产者组

        // 1. 创建一个原生的 DefaultMQProducer 实例
        DefaultMQProducer producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(nameServerAddr);

        System.out.println("诊断工具：已创建原生Producer，目标NameServer: " + nameServerAddr);

        try {
            // 2. 手动启动生产者
            // 启动过程会初始化与NameServer的连接
            System.out.println("诊断工具：正在启动原生Producer并连接NameServer...");
            producer.start();
            System.out.println("诊断工具：原生Producer启动成功！");

            // 3. 调用 fetchPublishMessageQueues 方法获取路由信息
            System.out.println("诊断工具：正在向NameServer查询Topic [" + testTopic + "] 的路由...");
            List<MessageQueue> queues = producer.fetchPublishMessageQueues(testTopic);

            // 4. 打印路由信息
            if (queues == null || queues.isEmpty()) {
                System.err.println("错误: 未能从NameServer获取到Topic [" + testTopic + "] 的任何路由信息！");
                System.err.println("请确认Topic名称正确，并且Broker已成功向NameServer注册。");
            } else {
                System.out.println("\n成功从NameServer获取到Topic [" + testTopic + "] 的路由信息！详情如下:");
                // 在这个原生API的场景下，我们无法直接获取IP，但可以打印出最关键的Broker Name
                for (MessageQueue queue : queues) {
                    System.out.println("--------------------------------------------------");
                    System.out.println("  - Broker Name: " + queue.getBrokerName());
                    System.out.println("  - Topic      : " + queue.getTopic());
                    System.out.println("  - Queue ID   : " + queue.getQueueId());
                }
                System.out.println("--------------------------------------------------");
                System.out.println("\n诊断结论：路由信息获取成功，表明NameServer工作正常，且Broker已注册。");
                System.out.println("下一步请确认客户端能否访问到Broker Name对应的物理IP地址和端口。");
            }

        } catch (Exception e) {
            System.err.println("错误: 在诊断过程中发生异常！");
            e.printStackTrace();
        } finally {
            // 5. 无论成功与否，都必须关闭生产者，释放资源
            if (producer != null) {
                System.out.println("\n诊断工具：正在关闭原生Producer...");
                producer.shutdown();
                System.out.println("诊断工具：原生Producer已关闭。");
            }
        }
        System.out.println("========= RocketMQ路由独立诊断结束 =========");
    }
}