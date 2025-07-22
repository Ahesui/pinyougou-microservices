# ---- Stage 1: Build ----
# 同样使用Maven镜像作为构建环境
FROM maven:3.8.4-openjdk-17 AS builder
WORKDIR /app
COPY . .
RUN mvn -f /app/pom.xml clean package spring-boot:repackage -pl pinyougou-user-service -DskipTests

# ---- Stage 2: Run ----
# 使用一个更小的、只包含Java运行时的镜像
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/pinyougou-user-service/target/*.jar app.jar
EXPOSE 18081
ENTRYPOINT ["java","-jar","/app/app.jar"]