# ---- Stage 1: Build ----
# 同样使用Maven镜像作为构建环境
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn -f /app/pom.xml clean package spring-boot:repackage -pl pinyougou-product-service -am -DskipTests

# ---- Stage 2: Run ----
# 使用一个轻量的、只包含Java运行环境的官方镜像作为最终的运行环境。
# 'slim' 版本不包含开发工具，体积更小。
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/pinyougou-product-service/target/*.jar app.jar
EXPOSE 18082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]