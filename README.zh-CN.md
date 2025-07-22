# 品优购Plus (Pinyougou-Plus): 生产级分布式电商平台

[**English**](./README.md) | [中文](./README.zh-CN.md)

![架构: 微服务](https://img.shields.io/badge/架构-微服务-orange)
![技术: Spring Cloud Alibaba](https://img.shields.io/badge/技术-Spring_Cloud_Alibaba-blueviolet)
![可观测性: 全栈方案](https://img.shields.io/badge/可观测性-全栈方案-yellow)
![DevOps: Docker & CI/CD](https://img.shields.io/badge/DevOps-Docker_&_CI/CD-blue)
![前端: Vue3 & Flutter](https://img.shields.io/badge/前端-Vue3_&_Flutter-green)

品优购Plus是一个全面的、用于展示现代化后端架构与全栈开发能力的分布式电商平台项目。该项目模拟了一个真实世界中的高并发、高可用的在线零售环境，旨在全面展示开发者在微服务、分布式系统以及DevOps工程实践方面的专业技能。

---

## ✨ 核心功能与技术亮点

本项目并非简单的CRUD应用，而是一个深度集成的系统，其核心亮点包括：

*   **高并发秒杀系统**: 实现了一个健壮的秒杀模块，综合运用了 **Redis分布式锁 (Redisson)** 与数据库乐观锁，在高并发场景下保证了数据的一致性与库存的准确性。
*   **异步通信与服务解耦**: 引入 **Apache RocketMQ** 对核心服务进行解耦，例如将用户注册后的通知处理异步化，显著提升了系统的吞吐能力与用户体验。
*   **全栈可观测性体系**: 搭建了完整的可观测性平台，利用 **ELK技术栈** 实现日志的集中管理与查询，**Prometheus & Grafana** 进行实时的性能指标监控与告警，**Apache SkyWalking** 提供分布式链路追踪，实现了对系统状态的快速故障诊断与性能瓶颈分析。
*   **全方位容器化部署**: 使用 **Docker** 对所有微服务及基础设施组件进行容器化，并通过 **Docker Compose** 进行服务编排，实现了一键化部署本地开发与测试环境。
*   **现代化全栈开发体验**: 基于 **Vue 3 & TypeScript** 开发了响应式的后台管理面板，并使用 **Flutter** 构建了跨平台的移动客户端，展示了端到端的交付能力。
*   **自动化CI/CD流水线**: 建立了CI/CD（持续集成/持续部署）流水线（基于GitLab CI/CD或Jenkins），实现了从代码提交到自动构建、测试和部署的全流程自动化，体现了现代敏捷与DevOps开发实践。

---

## 🏛️ 系统架构

平台基于微服务架构构建，服务间通过Nacos进行服务治理，采用OpenFeign进行同步调用，并通过RocketMQ实现异步通信。所有外部流量经由API网关统一路由。

![系统架构图](https://your-image-url/architecture.png)
*(**强烈建议**: 绘制一张专业的架构图并托管，然后替换上面的URL。)*

**核心组件:**
- **API网关 (`pinyougou-gateway`)**: 系统的统一入口，负责路由转发、身份认证、CORS跨域处理和限流等。
- **用户服务 (`pinyougou-user-service`)**: 管理用户认证、注册及个人资料。集成 **Redis** 缓存用户会话与热点数据。
- **商品服务 (`pinyougou-product-service`)**: 管理商品信息及核心的秒杀业务逻辑，利用 **Redis分布式锁** 防止超卖。
- **通知服务 (`pinyougou-notification-service`)**: 一个完全解耦的服务，作为 **RocketMQ** 的消费者，处理如发送欢迎短信等异步任务。
- **Nacos**: 作为服务注册发现中心与配置中心。
- **可观测性套件**: ELK, Prometheus, SkyWalking为系统提供了统一的健康状况与性能视图。

---

## 🚀 快速开始

### 环境依赖
- Java 17+ & Maven 3.6+
- Node.js 18+ & pnpm
- Flutter SDK 3.x
- Docker & Docker Compose

### 1. 启动基础设施服务
所有中间件及监控组件均由Docker Compose管理。
```bash
# 在项目根目录下执行
docker-compose up -d
```
此命令将一键启动 Nacos, Redis, RocketMQ, ELK, Prometheus, Grafana, 和 SkyWalking。

### 2. 运行后端微服务
每个微服务都可以在IDE中单独启动。或者，通过Docker Compose统一构建并运行：
```bash
# 此命令将为所有自定义服务构建Docker镜像并运行
docker-compose up -d --build
```

### 3. 运行前后端应用

**Web管理后台:**
```bash
cd pinyougou-admin-frontend
pnpm install
pnpm dev
# 访问 http://localhost:5173
```

**移动App:**
```bash
cd pinyougou_app
flutter pub get
flutter run
# 在连接的设备或模拟器上运行
```

### 主要服务访问地址
- **API 网关**: `http://localhost:18080`
- **Nacos 控制台**: `http://localhost:8848` (用户名: nacos, 密码: nacos)
- **Kibana (日志)**: `http://localhost:5601`
- **Grafana (监控)**: `http://localhost:3000` (用户名: admin, 密码: admin)
- **SkyWalking UI (链路)**: `http://localhost:19090`

---

## 🛠️ 技术深度解析

### 秒杀业务的分布式锁策略
为应对秒杀场景下的高并发请求，项目采用了基于 **Redisson的`RLock`** 实现的分布式锁。相较于数据库锁，Redis锁具备更高的性能和更低的数据库负载。通过设置锁的超时和`tryLock`机制，有效避免了死锁，并保证了请求的公平性。其内置的“看门狗”机制还能自动为即将过期的锁续期，防止了因业务执行时间过长导致锁被误释放的风险。

### 基于消息队列的服务解耦
在用户注册场景中，用户服务在完成数据库操作后，会立即向 **RocketMQ** 发送一条`USER_REGISTRATION_TOPIC`消息。这一设计使得主线程可以立刻释放，从而快速响应用户请求。通知服务作为独立的消费者订阅此主题，异步处理发送欢迎消息等非核心任务。该架构确保了即使通知系统发生故障或延迟，也**不会**影响核心的用户注册流程，极大地增强了系统的健壮性和弹性。

### 自动化的服务发现式监控
**Prometheus** 服务器被配置为使用**Nacos服务发现 (`nacos_sd_configs`)**。这一机制使得Prometheus能够动态地发现并抓取任何新注册到Nacos的微服务实例所暴露的监控指标。这免除了手动维护监控目标的繁琐工作，使得系统在水平扩展时，新的服务实例能够被自动纳入监控体系。

---

## 📄 开源许可证

本项目基于 [MIT License](LICENSE) 开源协议。详情请见 [LICENSE](LICENSE) 文件。