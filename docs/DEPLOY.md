# Admin-Boot 部署指南

本文档提供多种部署方式，包括 Docker 容器部署和传统服务器部署。

## 目录

- [环境要求](#环境要求)
- [Docker 部署](#docker-部署)
- [传统服务器部署](#传统服务器部署)
- [生产环境配置](#生产环境配置)
- [健康检查](#健康检查)
- [日志管理](#日志管理)
- [常见问题](#常见问题)

---

## 环境要求

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | 必须 |
| MySQL | 8.0+ | 必须 |
| Redis | 7.x | 必须 |
| Docker | 20.x+ | 可选 |

---

## Docker 部署

### 方式一：使用 docker-compose（推荐）

创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    container_name: admin-boot-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: ${DB_PASSWORD:-root123}
      MYSQL_DATABASE: admin_boot
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./init.sql:/docker-entrypoint-initdb.d/init.sql:ro
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci
      - --default-authentication-plugin=mysql_native_password

  redis:
    image: redis:7-alpine
    container_name: admin-boot-redis
    restart: always
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --appendonly yes

  admin-boot:
    build: .
    container_name: admin-boot
    restart: always
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=mysql
      - DB_PORT=3306
      - DB_NAME=admin_boot
      - DB_USERNAME=root
      - DB_PASSWORD=${DB_PASSWORD:-root123}
      - REDIS_HOST=redis
      - REDIS_PORT=6379
      - REDIS_PASSWORD=
      - JWT_SECRET=${JWT_SECRET:-your-production-secret-key-must-be-at-least-32-chars}
    depends_on:
      - mysql
      - redis
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3

volumes:
  mysql_data:
  redis_data:
```

启动服务：

```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f admin-boot

# 停止服务
docker-compose down
```

### 方式二：单独构建镜像

```bash
# 构建镜像
docker build -t admin-boot:latest .

# 运行容器
docker run -d -p 8080:8080 \
  --name admin-boot \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=localhost \
  -e DB_PORT=3306 \
  -e DB_NAME=admin_boot \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=root123 \
  -e REDIS_HOST=localhost \
  -e REDIS_PORT=6379 \
  -e JWT_SECRET=your-production-secret-key \
  admin-boot:latest
```

---

## 传统服务器部署

### 1. 环境准备

```bash
# 安装 JDK 17
sudo apt update
sudo apt install openjdk-17-jdk

# 验证安装
java -version

# 安装 Maven
sudo apt install maven

# 安装 MySQL 8.0
sudo apt install mysql-server

# 安装 Redis
sudo apt install redis-server
```

### 2. 数据库配置

```bash
# 登录 MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE admin_boot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 项目打包

```bash
# 克隆项目
git clone https://github.com/your-repo/admin-boot.git
cd admin-boot

# 构建项目
mvn clean package -DskipTests

# 构建产物
ls target/*.jar
```

### 4. 创建启动脚本

创建 `start.sh`：

```bash
#!/bin/bash

APP_NAME="admin-boot.jar"
APP_HOME="/opt/admin-boot"
LOG_FILE="${APP_HOME}/logs/app.log"
PID_FILE="${APP_HOME}/app.pid"

# 创建目录
mkdir -p ${APP_HOME}/logs

# JVM 参数
JAVA_OPTS="-server \
  -Xms512m \
  -Xmx1024m \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -Djava.security.egd=file:/dev/./urandom \
  -Dfile.encoding=UTF-8"

# 环境配置
export SPRING_PROFILES_ACTIVE=prod
export DB_HOST=localhost
export DB_PORT=3306
export DB_NAME=admin_boot
export DB_USERNAME=root
export DB_PASSWORD=your_password
export REDIS_HOST=localhost
export REDIS_PORT=6379
export JWT_SECRET=your-production-secret-key

# 启动应用
start() {
    if [ -f ${PID_FILE} ]; then
        echo "${APP_NAME} is already running!"
        exit 1
    fi

    echo "Starting ${APP_NAME}..."
    nohup java ${JAVA_OPTS} -jar ${APP_HOME}/${APP_NAME} \
        > ${LOG_FILE} 2>&1 &

    echo $! > ${PID_FILE}
    echo "${APP_NAME} started successfully!"
}

# 停止应用
stop() {
    if [ ! -f ${PID_FILE} ]; then
        echo "${APP_NAME} is not running!"
        return
    fi

    PID=$(cat ${PID_FILE})
    echo "Stopping ${APP_NAME} (PID: ${PID})..."
    kill ${PID}

    sleep 5
    rm -f ${PID_FILE}
    echo "${APP_NAME} stopped!"
}

# 重启应用
restart() {
    stop
    sleep 2
    start
}

# 查看状态
status() {
    if [ -f ${PID_FILE} ]; then
        PID=$(cat ${PID_FILE})
        if ps -p ${PID} > /dev/null; then
            echo "${APP_NAME} is running (PID: ${PID})"
        else
            echo "${APP_NAME} is not running"
            rm -f ${PID_FILE}
        fi
    else
        echo "${APP_NAME} is not running"
    fi
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    status)
        status
        ;;
    *)
        echo "Usage: $0 {start|stop|restart|status}"
        exit 1
        ;;
esac
```

设置权限并启动：

```bash
chmod +x start.sh
sudo mv start.sh /opt/admin-boot/
sudo /opt/admin-boot/start.sh start
```

### 5. 配置 Systemd 服务

创建服务文件 `/etc/systemd/system/admin-boot.service`：

```ini
[Unit]
Description=Admin Boot Application
After=network.target mysql.service redis.service
Wants=mysql.service redis.service

[Service]
Type=simple
User=root
WorkingDirectory=/opt/admin-boot
ExecStart=/usr/bin/java -server -Xms512m -Xmx1024m -jar admin-boot.jar
ExecStop=/bin/kill -SIGTERM $MAINPID
Restart=on-failure
RestartSec=10

Environment=JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
Environment=SPRING_PROFILES_ACTIVE=prod

[Install]
WantedBy=multi-user.target
```

启用服务：

```bash
sudo systemctl daemon-reload
sudo systemctl enable admin-boot
sudo systemctl start admin-boot
sudo systemctl status admin-boot
```

### 6. 配置 Nginx 反向代理（可选）

```nginx
server {
    listen 80;
    server_name admin.example.com;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 超时配置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # WebSocket 支持（如果需要）
    location /ws {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
}

# HTTPS 配置（生产环境推荐）
server {
    listen 443 ssl http2;
    server_name admin.example.com;

    ssl_certificate /etc/nginx/ssl/cert.pem;
    ssl_certificate_key /etc/nginx/ssl/key.pem;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

---

## 生产环境配置

### 1. application-prod.yml

```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
      idle-timeout: 300000
      connection-timeout: 20000
      max-lifetime: 1800000
  data:
    redis:
      database: 1
      lettuce:
        pool:
          max-active: 20
          max-idle: 10
          min-idle: 5

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl

jwt:
  secret: ${JWT_SECRET}  # 必须配置复杂的密钥

rate-limit:
  enabled: true
```

### 2. 环境变量

| 变量名 | 说明 | 示例 |
|--------|------|------|
| DB_HOST | 数据库地址 | mysql.prod.internal |
| DB_PORT | 数据库端口 | 3306 |
| DB_NAME | 数据库名称 | admin_boot |
| DB_USERNAME | 数据库用户名 | admin_boot |
| DB_PASSWORD | 数据库密码 | (复杂密码) |
| REDIS_HOST | Redis 地址 | redis.prod.internal |
| REDIS_PORT | Redis 端口 | 6379 |
| REDIS_PASSWORD | Redis 密码 | (密码) |
| JWT_SECRET | JWT 密钥 | (32+位复杂密钥) |
| FILE_UPLOAD_PATH | 文件上传路径 | /data/admin-boot/uploads |

### 3. 安全建议

- [ ] 修改默认数据库密码
- [ ] 配置复杂的 JWT_SECRET
- [ ] 启用防火墙，只开放必要端口
- [ ] 配置 SSL/TLS 证书
- [ ] 启用接口限流
- [ ] 开启 Redis 密码认证
- [ ] 定期备份数据库

---

## 健康检查

### 端点

| 端点 | 说明 |
|------|------|
| GET /actuator/health | 健康检查 |
| GET /actuator/info | 应用信息 |
| GET /actuator/metrics | 监控指标 |

### Docker 健康检查

```yaml
healthcheck:
  test: ["CMD", "curl", "-f", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 10s
  retries: 3
  start_period: 60s
```

---

## 日志管理

### 日志配置

```yaml
logging:
  file:
    path: /var/log/admin-boot
    name: ${logging.file.path}/application.log
  logback:
    rollingpolicy:
      max-file-size: 100MB
      max-history: 30
```

### 日志归档

```bash
# 压缩日志
find /var/log/admin-boot -name "*.log" -mtime +1 -exec gzip {} \;

# 删除旧日志
find /var/log/admin-boot -name "*.gz" -mtime +90 -delete
```

---

## 常见问题

### 1. 启动失败，数据库连接超时

```bash
# 检查 MySQL 是否运行
mysql -u root -p -e "SELECT 1"

# 检查网络连接
telnet mysql_host 3306
```

### 2. Redis 连接失败

```bash
# 检查 Redis 是否运行
redis-cli ping
```

### 3. 内存不足

调整 JVM 参数：

```bash
java -Xms256m -Xmx512m -jar admin-boot.jar
```

### 4. 接口 401 未授权

- 检查 Token 是否过期
- 检查 Token 格式是否正确（Bearer xxx）
- 检查 JWT_SECRET 配置

---

## 扩展阅读

- [架构设计文档](architecture-design.md)
- [开发指南](DEV-GUIDE.md)
- [Spring Boot 部署](https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html)
- [Docker 部署](https://docs.docker.com/compose/)
