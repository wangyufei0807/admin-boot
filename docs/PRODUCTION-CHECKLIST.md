# 生产部署检查清单

本文档列出了部署到生产环境前必须完成的所有检查项。

## 🔴 关键安全检查（必须完成）

### 环境变量配置

- [ ] **JWT_SECRET** 已配置且长度 >= 256 bits
  ```bash
  # 检查命令
  echo -n "${JWT_SECRET}" | wc -c  # 输出应该 >= 32
  ```
  - [ ] 密钥使用强随机值
  - [ ] 密钥与其他环境不同
  - [ ] 密钥不存储在代码库中

- [ ] **数据库密码**已设置
  - [ ] DB_USERNAME 已配置
  - [ ] DB_PASSWORD 已配置（强密码 >= 12 字符）
  - [ ] 密码包含大小写字母、数字、特殊符号
  - [ ] 不能使用默认密码

- [ ] **Redis 密码**已设置
  - [ ] REDIS_PASSWORD 已配置
  - [ ] Redis 启用认证
  - [ ] Redis 绑定到 127.0.0.1（不暴露到公网）

### 配置文件检查

- [ ] 生产环境使用 `application-prod.yml`
  ```bash
  # 检查命令
  grep -n "skyware\|root123\|secret-key" admin-system/src/main/resources/*.yml
  # 输出应该为空（没有硬编码密钥）
  ```

- [ ] 所有敏感配置通过环境变量注入
  - [ ] 数据库 URL/用户名/密码
  - [ ] Redis 主机/密码
  - [ ] JWT 密钥
  - [ ] 第三方 API Key

- [ ] 关闭 API 文档
  - [ ] Knife4j 已禁用（生产环境）
  - [ ] Swagger UI 已禁用（生产环境）
  - [ ] Actuator 端点已限制

### 数据库检查

- [ ] 数据库已创建
  ```sql
  CREATE DATABASE admin_boot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```

- [ ] 初始化脚本已运行
  - [ ] 所有表已创建
  - [ ] 初始数据已导入
  - [ ] 索引已建立

- [ ] 数据库用户权限配置
  - [ ] 应用用户无删除数据库权限
  - [ ] 只授予必需的权限
  - [ ] 禁用了 root 用户远程登录

- [ ] 数据库备份
  - [ ] 备份脚本已准备
  - [ ] 备份计划已设置（每天自动备份）
  - [ ] 备份恢复已测试

### 日志检查

- [ ] 日志级别设置为 INFO
  ```yaml
  logging:
    level:
      root: INFO
      com.admin: INFO
  ```

- [ ] SQL 日志已关闭
  ```yaml
  mybatis-plus:
    configuration:
      log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl
  ```

- [ ] 日志输出到文件
  - [ ] 日志路径已配置（/var/log/admin-boot）
  - [ ] 日志文件大小限制已设置（100MB）
  - [ ] 日志保留天数已设置（30 天）
  - [ ] 日志目录权限为 755

- [ ] 敏感信息脱敏
  - [ ] 密码不记录在日志中
  - [ ] Token 不完整记录在日志中
  - [ ] 用户隐私数据已脱敏

## 🟠 网络安全检查

### HTTPS/TLS 配置

- [ ] SSL 证书已申请
  - [ ] 证书来自受信任的 CA
  - [ ] 证书有效期检查（> 30 天）
  - [ ] 证书已导入到服务器

- [ ] HTTPS 已启用
  ```yaml
  server:
    ssl:
      key-store: /path/to/keystore.p12
      key-store-password: ${SSL_KEYSTORE_PASSWORD}
      key-store-type: PKCS12
  ```

- [ ] HTTP 已禁用或重定向到 HTTPS

### 防火墙配置

- [ ] 只暴露必要的端口
  - [ ] 8080（HTTPS 应用端口）
  - [ ] 3306（MySQL，仅允许内网）
  - [ ] 6379（Redis，仅允许内网）

- [ ] SSH 端口已更改（不使用默认 22）

- [ ] 入站规则已配置
  ```bash
  # 示例
  ufw allow 22/tcp
  ufw allow 80/tcp
  ufw allow 443/tcp
  ufw allow 8080/tcp
  ufw enable
  ```

### CORS 配置

- [ ] CORS 已限制到特定域名
  ```yaml
  cors:
    allowed-origins:
      - https://your-domain.com
      - https://api.your-domain.com
  ```

- [ ] 不使用通配符 `*`

- [ ] 允许的请求头已明确列出

## 🟡 性能和容量检查

### 数据库性能

- [ ] 索引已优化
  ```sql
  -- 检查慢查询
  SELECT * FROM mysql.slow_log LIMIT 10;
  ```

- [ ] 连接池配置
  ```yaml
  datasource:
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50  # 生产环境调整
  ```

- [ ] 查询性能已优化（慢查询 < 1s）

### Redis 配置

- [ ] Redis 内存使用量监控
  ```bash
  redis-cli INFO memory
  ```

- [ ] Redis 持久化已启用
  - [ ] RDB 备份已配置
  - [ ] AOF 日志已启用（可选）

### JVM 参数调优

- [ ] Heap 大小已配置
  ```bash
  export JAVA_OPTS="-server -Xms1g -Xmx2g -XX:+UseG1GC"
  ```

- [ ] 垃圾回收已优化
  - [ ] 使用 G1GC（推荐）
  - [ ] GC 日志已启用
  - [ ] GC 暂停时间 < 200ms

## 🟢 监控和告警检查

### 应用监控

- [ ] Actuator 端点已配置
  ```bash
  curl http://localhost:8080/actuator/health
  ```

- [ ] 健康检查已集成
  - [ ] 数据库连接检查
  - [ ] Redis 连接检查
  - [ ] 磁盘空间检查

### 日志监控

- [ ] 日志聚合已配置（ELK/Loki）
  - [ ] 日志已发送到中央日志系统
  - [ ] 日志告警已设置

- [ ] 错误监控已配置（Sentry）
  - [ ] 异常自动上报
  - [ ] 告警已设置

### 性能监控

- [ ] 应用性能监控（APM）已配置（Skywalking/Pinpoint）
  - [ ] 链路追踪已启用
  - [ ] 慢链路告警已设置

### 系统资源监控

- [ ] CPU 使用率监控
  - [ ] 告警阈值：> 80%

- [ ] 内存使用率监控
  - [ ] 告警阈值：> 85%

- [ ] 磁盘使用率监控
  - [ ] 告警阈值：> 90%

- [ ] 网络流量监控
  - [ ] 带宽告警已设置

## 📋 文档和流程检查

### 文档

- [ ] 部署文档已准备
- [ ] 运维手册已准备
- [ ] 故障排查指南已准备
- [ ] API 文档已生成
- [ ] 数据库设计文档已准备

### 流程

- [ ] 备份恢复流程已建立
- [ ] 故障转移流程已建立
- [ ] 应急响应计划已准备
- [ ] 回滚流程已测试
- [ ] 更新升级流程已测试

## 🚀 部署流程

### 前置准备

```bash
# 1. 检查所有环境变量
env | grep -E 'JWT|DB|REDIS'

# 2. 验证配置文件
grep -r 'password' admin-system/src/main/resources/application-prod.yml

# 3. 构建应用
mvn clean package -DskipTests -P prod

# 4. 测试构建产物
java -jar admin-boot.jar --spring.profiles.active=test
```

### 部署步骤

```bash
# 1. 停止旧服务
sudo systemctl stop admin-boot

# 2. 备份数据库
mysqldump -u root -p admin_boot > backup_$(date +%Y%m%d).sql

# 3. 上传新版本
scp admin-boot.jar server:/opt/admin-boot/

# 4. 启动新服务
sudo systemctl start admin-boot

# 5. 验证健康状态
curl http://localhost:8080/actuator/health

# 6. 查看日志
tail -f /var/log/admin-boot/application.log
```

### 部署验证

- [ ] 应用成功启动（检查日志）
- [ ] 健康检查通过
- [ ] API 端点可访问
- [ ] 数据库连接正常
- [ ] Redis 连接正常
- [ ] 登录功能正常
- [ ] 核心业务流程可用

### 监控验证

```bash
# 检查 CPU 使用率
top -b -n 1 | head -20

# 检查内存使用率
free -h

# 检查磁盘使用率
df -h

# 检查日志是否有错误
grep ERROR /var/log/admin-boot/application.log

# 检查应用进程
ps aux | grep admin-boot
```

## 🔄 生产后操作

### 定期维护

- [ ] 每周检查日志文件大小
- [ ] 每月检查数据库备份完整性
- [ ] 每月查看性能监控报告
- [ ] 每季度进行安全审计
- [ ] 每季度更新依赖库（检查安全补丁）

### 应急响应

- [ ] 建立 24/7 值班制度
- [ ] 准备故障恢复脚本
- [ ] 定期演练故障转移
- [ ] 准备回滚方案

## 📞 联系信息

- **技术支持**：[your-email]
- **运维团队**：[ops-email]
- **紧急联系**：[emergency-contact]

---

**最后更新**：2024-05-26

**部署者**：___________

**部署时间**：___________

**部署版本**：___________

**检查清单完成率**：_____/100%
