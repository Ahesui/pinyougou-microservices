# Pinyougou-Plus: A Production-Grade Distributed E-commerce Platform

[English](./README.md) | [**中文**](./README.zh-CN.md)

![Architecture: Microservices](https://img.shields.io/badge/Architecture-Microservices-orange)
![Tech: Spring Cloud Alibaba](https://img.shields.io/badge/Tech-Spring_Cloud_Alibaba-blueviolet)
![Observability: Full Stack](https://img.shields.io/badge/Observability-Full_Stack-yellow)
![DevOps: Docker & CI/CD](https://img.shields.io/badge/DevOps-Docker_&_CI/CD-blue)
![Frontend: Vue3 & Flutter](https://img.shields.io/badge/Frontend-Vue3_&_Flutter-green)

Pinyougou-Plus is a comprehensive, distributed e-commerce platform designed to showcase modern backend architecture and full-stack development capabilities. This project simulates a real-world, high-concurrency, and highly available online retail environment, demonstrating proficiency in microservices, distributed systems, and DevOps practices.

---

## ✨ Core Features & Technical Highlights

This project is not just a simple CRUD application; it's an integrated system featuring:

*   **High-Concurrency Seckill System**: Implemented a robust seckill (flash sale) module utilizing **Redis distributed locking (Redisson)** and database optimistic locking to ensure data consistency and handle high traffic bursts.
*   **Asynchronous Communication & Decoupling**: Leveraged **Apache RocketMQ** to decouple core services, such as processing user registration notifications asynchronously, significantly improving system throughput and user experience.
*   **Full-Stack Observability**: Established a comprehensive observability platform using the **ELK stack** for centralized logging, **Prometheus & Grafana** for real-time metrics monitoring, and **Apache SkyWalking** for distributed tracing, enabling rapid fault diagnosis and performance analysis.
*   **Containerized Deployment**: Fully containerized all microservices and infrastructure components using **Docker**, and orchestrated the entire environment with **Docker Compose**, achieving one-click deployment for development and testing.
*   **Modern Full-Stack Experience**: Developed a responsive management dashboard with **Vue 3 & TypeScript** and a cross-platform mobile application with **Flutter**, demonstrating end-to-end delivery capability.
*   **Automated CI/CD Pipeline**: Set up a CI/CD pipeline (using GitLab CI/CD or Jenkins) to automate the build, testing, and deployment processes, reflecting modern agile and DevOps practices.

---

## 🏛️ System Architecture

The platform is built on a microservices architecture, with services communicating via both synchronous (OpenFeign) and asynchronous (RocketMQ) patterns. All external traffic is routed through a central API Gateway.

![System Architecture Diagram](https://your-image-url/architecture.png)
*(**Note**: It is highly recommended to draw a professional architecture diagram and host it online, then replace the URL above.)*

**Key Components:**
- **API Gateway (`pinyougou-gateway`)**: The single entry point for all clients. Handles routing, authentication, CORS, and rate limiting.
- **User Service (`pinyougou-user-service`)**: Manages user authentication, registration, and user profiles. Integrated with **Redis** for caching user sessions and data.
- **Product Service (`pinyougou-product-service`)**: Manages product information and the core seckill logic, utilizing **Redis distributed locks** to prevent overselling.
- **Notification Service (`pinyougou-notification-service`)**: A decoupled service that consumes messages from **RocketMQ** to handle asynchronous tasks like sending welcome emails or SMS.
- **Nacos**: Serves as the service registry, discovery center, and configuration management hub.
- **Observability Stack**: ELK, Prometheus, and SkyWalking are integrated to provide a unified view of the system's health and performance.

---

## 🚀 Getting Started

### Prerequisites
- Java 17+ & Maven 3.6+
- Node.js 18+ & pnpm
- Flutter SDK 3.x
- Docker & Docker Compose

### 1. Launch Infrastructure Services
The entire infrastructure stack is managed by Docker Compose.
```bash
# From the project root directory
docker-compose up -d
```
This command will start Nacos, Redis, RocketMQ, ELK, Prometheus, Grafana, and SkyWalking.

### 2. Run Backend Microservices
Each microservice can be run from your IDE (e.g., IntelliJ IDEA, VS Code). Alternatively, build and run all services via Docker Compose:
```bash
# This will build Docker images for all custom services and run them
docker-compose up -d --build
```

### 3. Run Web & Mobile Frontends

**Web Admin Dashboard:**
```bash
cd pinyougou-admin-frontend
pnpm install
pnpm dev
# Access at http://localhost:5173
```

**Mobile App:**
```bash
cd pinyougou_app
flutter pub get
flutter run
# Runs on a connected device or simulator
```

### Key Service Endpoints
- **API Gateway**: `http://localhost:18080`
- **Nacos Console**: `http://localhost:8848` (u: nacos, p: nacos)
- **Kibana (Logs)**: `http://localhost:5601`
- **Grafana (Metrics)**: `http://localhost:3000` (u: admin, p: admin)
- **SkyWalking UI (Traces)**: `http://localhost:19090`

---

## 🛠️ Technical Deep Dive

### Distributed Locking Strategy for Seckill
To handle the high concurrency of flash sales, a distributed lock was implemented using **Redisson's `RLock`**. This approach was chosen over database locks for its superior performance and reduced database contention. The `tryLock` mechanism with a timeout prevents deadlocks and ensures fair access, while the built-in watchdog functionality automatically extends the lock lease, preventing premature release during long-running operations.

### Service Decoupling with Message Queues
Upon user registration, the User Service publishes a `USER_REGISTRATION_TOPIC` message to **RocketMQ**. This immediately frees up the main thread, providing a swift response to the user. The Notification Service, as an independent consumer, subscribes to this topic to handle tasks like sending welcome messages. This architecture ensures that a failure or delay in the notification system will **not** impact the core user registration process, significantly improving system resilience.

### Automated Service Discovery in Monitoring
The **Prometheus** server is configured to use **Nacos service discovery (`nacos_sd_configs`)**. This allows Prometheus to dynamically discover and scrape metrics from any new microservice instance that registers with Nacos, eliminating the need for manual configuration updates and enabling seamless scalability.

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.