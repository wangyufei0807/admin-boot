# Admin-Boot Docker 构建文件

# 基础镜像
FROM eclipse-temurin:17-jdk-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制 Maven 配置文件
COPY pom.xml .
COPY admin-common/pom.xml admin-common/
COPY admin-system/pom.xml admin-system/
COPY admin-generator/pom.xml admin-generator/

# 下载依赖（利用 Docker 缓存）
RUN mvn dependency:go-offline -B || true

# 复制源代码
COPY admin-common/src admin-common/src
COPY admin-system/src admin-system/src
COPY admin-generator/src admin-generator/src
COPY resources resources
COPY pom.xml .

# 构建项目
RUN mvn clean package -DskipTests -B

# 运行镜像
FROM eclipse-temurin:17-jre-alpine

# 安全相关
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

WORKDIR /app

# 从构建阶段复制产物
COPY --from=builder /app/admin-system/target/*.jar app.jar
COPY --from=builder /app/resources ./resources

# 创建上传目录
RUN mkdir -p /data/upload && chown -R appuser:appgroup /data

# 切换到非 root 用户
USER appuser

# 环境变量
ENV JAVA_OPTS="-server -Xms256m -Xmx512m -XX:+UseG1GC -Djava.security.egd=file:/dev/./urandom"
ENV SPRING_PROFILES_ACTIVE=prod

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
