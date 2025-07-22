# ---- Stage 1: Build ----
# 同样使用Maven镜像作为构建环境
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn -f /app/pom.xml clean package  spring-boot:repackage -pl pinyougou-notification-service -am -DskipTests

# ---- Stage 2: Run ----
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/pinyougou-notification-service/target/*.jar app.jar
EXPOSE 18083
ENTRYPOINT ["java", "-jar", "/app/app.jar"]