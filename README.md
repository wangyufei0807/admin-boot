# Admin-Boot

基于 Spring Boot + MyBatis-Plus 的快速开发脚手架，提供完整的 RBAC 权限管理功能，开箱即用。

## 特性

- **RBAC 权限管理**：基于角色的访问控制，支持菜单权限和按钮权限
- **JWT 认证**：无状态 Token 认证，支持 Access Token 和 Refresh Token
- **代码生成器**：一键生成 CRUD 代码，快速开发新模块
- **统一响应**：标准化的 API 响应格式
- **操作日志**：敏感操作自动记录日志
- **接口限流**：防止暴力破解和接口滥用
- **多环境配置**：dev / test / prod 环境配置分离
- **API 文档**：集成 Knife4j，提供 Swagger UI

## 技术栈

| 技术 | 说明 |
|------|------|
| Spring Boot 3.2.5 | 核心框架 |
| MyBatis-Plus 3.5.7 | ORM 框架 |
| jjwt 0.12.5 | Token 认证 |
| Redis 7.x | 缓存、Token 存储 |
| MySQL 8.0 | 数据库 |
| Knife4j 4.5.0 | API 文档 |

## 项目结构

```
admin-boot
├── admin-common          # 公共模块
├── admin-system         # 系统模块
├── admin-generator      # 代码生成器
└── resources/          # 配置文件
```

详细结构请查看 [架构设计文档](docs/architecture-design.md)

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+
- Redis 7.x

### 1. 克隆项目

```bash
git clone https://github.com/your-repo/admin-boot.git
cd admin-boot
```

### 2. 配置数据库

修改 `resources/application-dev.yml` 或创建 `application-local.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/admin_boot?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
      password: your_redis_password
```

### 3. 创建数据库

```sql
CREATE DATABASE admin_boot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 启动项目

```bash
# 开发环境启动
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 或打包后运行
mvn clean package
java -jar admin-boot.jar --spring.profiles.active=dev
```

### 5. 访问系统

| 地址 | 说明 |
|------|------|
| http://localhost:8080 | 应用地址 |
| http://localhost:8080/doc.html | Knife4j API 文档 |
| http://localhost:8080/swagger-ui/index.html | Swagger UI |

### 6. 登录系统

| 用户名 | 密码 |
|--------|------|
| admin | admin123 |

## 接口示例

### 登录

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

响应：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expires_in": 900
  }
}
```

### 获取用户列表

```bash
curl -X GET "http://localhost:8080/api/system/user/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer <access_token>"
```

## 开发新模块

### 1. 设计数据库表

```sql
CREATE TABLE sys_product (
    id              BIGINT          NOT NULL   PRIMARY KEY AUTO_INCREMENT,
    product_name    VARCHAR(100)    NOT NULL   COMMENT '产品名称',
    price           DECIMAL(10,2)   NOT NULL   COMMENT '价格',
    stock           INT             DEFAULT 0  COMMENT '库存',
    status          TINYINT        DEFAULT 1  COMMENT '状态',
    create_time     DATETIME        DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME        DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. 使用代码生成器

修改 `admin-generator/src/main/resources/generator.yml` 配置数据库连接，然后运行生成器。

### 3. 注册菜单

在 `sys_menu` 表中插入菜单数据，给角色分配权限。

## Docker 部署

```bash
# 构建镜像
docker build -t admin-boot .

# 运行容器
docker run -d -p 8080:8080 \
  -e DB_HOST=mysql \
  -e DB_PORT=3306 \
  -e DB_NAME=admin_boot \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=your_password \
  -e REDIS_HOST=redis \
  -e REDIS_PORT=6379 \
  -e JWT_SECRET=your-secret-key \
  --name admin-boot \
  admin-boot
```

详细部署文档请查看 [DEPLOY.md](docs/DEPLOY.md)

## 开发指南

本地开发环境搭建请查看 [DEV-GUIDE.md](docs/DEV-GUIDE.md)

## 接口文档

启动项目后访问：
- Knife4j: http://localhost:8080/doc.html
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## 目录规范

```
admin-system/src/main/java/com/admin/
├── {module}/       # 业务模块（user/、role/、menu/...）
│   ├── controller/   # 控制器
│   ├── service/       # 服务层
│   │   └── impl/      # 服务实现
│   ├── mapper/        # 数据层
│   ├── entity/        # 实体类
│   ├── dto/           # 请求参数
│   ├── query/         # 查询参数
│   ├── vo/            # 响应视图
│   └── converter/     # 对象转换器
```

## License

MIT License
