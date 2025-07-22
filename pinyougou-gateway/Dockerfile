# ---- Stage 1: Build ----
FROM maven:3.8.4-openjdk-17 AS builder
# 设置在容器内的工作目录
WORKDIR /app
# 复制整个项目到容器中。
# 这样做能确保所有父子模块依赖都被正确处理。
COPY . .
# 执行Maven打包命令。
RUN mvn -f /app/pom.xml clean package  spring-boot:repackage -pl pinyougou-gateway -am -DskipTests

# ---- Stage 2: Run ----
# 使用相同的轻量的、只包含Java运行环境的官方镜像作为最终的运行环境。
# 'slim' 版本不包含开发工具，体积更小。
FROM openjdk:17-jdk-slim
# 设置工作目录
WORKDIR /app
# 从构建阶段复制gateway的jar包
COPY --from=builder /app/pinyougou-gateway/target/*.jar app.jar
# 声明网关的端口
EXPOSE 18080
# 启动命令
ENTRYPOINT ["java", "-jar", "/app/app.jar"]