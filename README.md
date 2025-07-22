# Pinyougou-Plus (品优购 Plus) - 分布式电商平台实践项目

![Java](https://img.shields.io/badge/Java-17-blue.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2022.x-green.svg)
![Vue.js](https://img.shields.io/badge/Vue.js-3.x-blue.svg)
![Flutter](https://img.shields.io/badge/Flutter-3.x-00B4D0.svg)
![Docker](https://img.shields.io/badge/Docker-Powered-blue.svg)
![License](https://img.shields.io/badge/License-MIT-brightgreen.svg)

`Pinyougou-Plus` 是一个全面的、循序渐进的分布式电商平台学习项目。它旨在通过实战，引导开发者从构建一个简单的单体应用开始，逐步演进为一个功能完善、高可用、可观测的现代化微服务架构系统。项目覆盖了从后端底层原理到前端、移动端，再到自动化运维的全栈技术链路。

---

## 📖 项目目标与学习路径

本项目旨在提供一个“沙盒”，让开发者可以在一个完整的业务场景中，深入实践和理解以下核心技术与理念：

- **Java 核心技术**: 深入理解 JVM 原理、GC 机制，熟练掌握封装、继承、多态、SPI 等。
- **软件设计**: 掌握常用设计模式，如工厂、策略、单例、观察者模式。
- **并发与分布式**: 掌握多线程、线程安全，深入理解分布式系统设计、微服务架构、服务治理与远程调用。
- **核心中间件**: 精通 Nacos, RocketMQ, Redis 等常用中间件的原理与实战。
- **可观测性**: 掌握基于 ELK, Prometheus, SkyWalking 的分布式日志、监控与链路追踪体系。
- **数据存储**: 具备 MySQL 数据库设计、SQL 性能调优、索引与锁机制的深入理解，并熟悉 NoSQL 数据库的使用。
- **全栈开发**: 熟练掌握 Vue 3、TypeScript 进行 Web 前端开发，以及使用 Flutter 进行跨平台移动端开发。
- **工程化与自动化**: 熟练掌握 Git 版本控制、敏捷开发流程，并使用 Docker 和 CI/CD 实现应用的容器化与自动化部署。

## ✨ 技术栈

### 后端 (Backend)

- **核心框架**: Spring Boot 3.x, Spring Cloud 2022.x, Spring Cloud Alibaba
- **服务治理**: Nacos (服务注册、发现与配置中心)
- **远程调用**: OpenFeign (声明式 HTTP 客户端)
- **API 网关**: Spring Cloud Gateway
- **消息队列**: Apache RocketMQ
- **数据库**: MySQL 8.x
- **缓存与锁**: Redis, Redisson
- **持久层**: Spring Data JPA, Hibernate
- **安全**: Spring Security (用于密码加密)

### 前端 (Frontend)

- **Web 管理后台**: Vue 3, TypeScript, Vite, Element Plus, Axios, Vue Router
- **移动 App**: Flutter, Dart, Dio, Provider

### 可观测性 (Observability)

- **日志**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **监控**: Prometheus, Grafana
- **链路追踪**: Apache SkyWalking

### 运维与工程化 (DevOps)

- **容器化**: Docker, Docker Compose
- **版本控制**: Git, GitLab
- **CI/CD**: GitLab CI/CD (或 Jenkins)
- **开发流程**: Agile

---

## 🏛️ 系统架构

本项目采用微服务架构，服务之间通过 Nacos 进行注册与发现，外部流量经由 API 网关统一路由。服务间通过 OpenFeign 进行同步调用，或通过 RocketMQ 进行异步解耦。

```
+----------------+      +------------------+      +-------------------+      +---------------------+
|  Web Frontend  |----->|                  |----->|                   |----->|   User Service      |
| (Vue 3)        |      |      Nginx       |      |    API Gateway    |      +---------------------+
+----------------+      |   (Load Balancer)|      | (Routing, CORS)   |
                        |                  |      |                   |      +---------------------+
+----------------+      +--------+---------+      +---------+---------+----->|  Product Service    |
| Mobile App     |----->|                  |                |                | (Seckill, Locking)  |
| (Flutter)      |      +------------------+                |                +---------------------+
+----------------+                                          |
                                                            |                +---------------------+
                                                            +--------------->| Notification Service|
                                                                             | (MQ Consumer)       |
                                                                             +---------------------+

+---------------------+      +---------------------+      +---------------------+      +---------------------+
|       Nacos         |<---->|      Redis          |<---->|      MySQL          |<---->|      RocketMQ       |
| (Service Discovery) |      | (Cache, Lock)       |      | (Data Persistence)  |      |  (Async, Decouple)  |
+---------------------+      +---------------------+      +---------------------+      +---------------------+

+---------------------------------------------------------------------------------------------------------+
|                                           Observability Platform                                        |
|      +---------------------+      +-----------------------------+      +------------------------------+ |
|      |    ELK Stack        |      |  Prometheus + Grafana       |      |      SkyWalking              | |
|      | (Centralized Logs)  |      | (Metrics Monitoring)        |      | (Distributed Tracing)        | |
|      +---------------------+      +-----------------------------+      +------------------------------+ |
+---------------------------------------------------------------------------------------------------------+
```

---

## 🚀 快速开始

### 环境准备

- JDK 17+
- Maven 3.6+
- Node.js 18+ & pnpm
- Flutter SDK 3.x
- Docker & Docker Compose
- IDE (IntelliJ IDEA / VS Code)

### 1. 启动所有基础设施服务

本项目所有中间件和监控组件均已通过 Docker Compose 进行编排。

```bash
# 在项目根目录下执行
docker-compose up -d
```

这将一键启动 Nacos, Redis, RocketMQ, ELK, Prometheus, Grafana, SkyWalking 等所有服务。

### 2. 启动后端微服务

为每个 Java 微服务（如`pinyougou-user-service`, `pinyougou-product-service`, `pinyougou-gateway`等）在 IDE 中创建启动配置，并依次启动。

**注意**: 你也可以通过`docker-compose up --build`来统一构建并启动所有自定义的微服务。

### 3. 启动 Web 管理后台

```bash
cd pinyougou-admin-frontend
pnpm install
pnpm dev
```

访问 `http://localhost:5173`。

### 4. 启动移动 App

1. 确保已启动一个模拟器或连接了真机。
2. 检查 `pinyougou_app/lib/services/api_service.dart` 中的 `getBaseUrl()` 是否配置正确。
3. 执行：
   ```bash
   cd pinyougou_app
   flutter pub get
   flutter run
   ```

### 服务访问地址

- **API 网关**: `http://localhost:18080`
- **Nacos 控制台**: `http://localhost:8848` (nacos/nacos)
- **Kibana (日志)**: `http://localhost:5601`
- **Prometheus**: `http://localhost:9090`
- **Grafana (监控)**: `http://localhost:3000` (admin/admin)
- **SkyWalking UI**: `http://localhost:19090`

---

## 🤝 贡献

欢迎对本项目提出改进意见和建议。你可以通过以下方式贡献：

- 提交 [Issue](<link-to-your-repo>/issues) 报告 bug 或提出功能建议。
- 提交 [Pull Request](<link-to-your-repo>/pulls) 改进代码或文档。

## 📄 许可证

本项目采用 [MIT License](LICENSE) 开源协议。
