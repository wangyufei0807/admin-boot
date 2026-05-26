# Admin-Boot 开发指南

本文档帮助开发者在本地搭建开发环境，快速上手项目开发。

## 目录

- [环境准备](#环境准备)
- [项目导入](#项目导入)
- [数据库初始化](#数据库初始化)
- [配置修改](#配置修改)
- [启动项目](#启动项目)
- [开发流程](#开发流程)
- [常用命令](#常用命令)
- [IDE 配置](#ide-配置)
- [调试技巧](#调试技巧)

---

## 环境准备

### 必须安装

| 软件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | 必须，可从 [Adoptium](https://adoptium.net/) 下载 |
| Maven | 3.8+ | 必须，可从 [Maven官网](https://maven.apache.org/download.cgi) 下载 |
| MySQL | 8.0+ | 必须，可从 [MySQL官网](https://dev.mysql.com/downloads/mysql/) 下载 |
| Redis | 7.x | 必须，可从 [Redis官网](https://redis.io/download/) 下载 |

### 可选安装

| 软件 | 说明 |
|------|------|
| Docker Desktop | 用于运行 MySQL 和 Redis |
| Git | 版本控制 |
| IntelliJ IDEA / VS Code | IDE |

### 使用 Docker 运行数据库（推荐）

如果不想本地安装 MySQL 和 Redis，可以使用 Docker：

```bash
# 启动 MySQL
docker run -d \
  --name admin-mysql \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=admin_boot \
  mysql:8.0

# 启动 Redis
docker run -d \
  --name admin-redis \
  -p 6379:6379 \
  redis:7-alpine

# 停止容器
docker stop admin-mysql admin-redis

# 删除容器
docker rm admin-mysql admin-redis
```

---

## 项目导入

### 1. 克隆项目

```bash
git clone https://github.com/your-repo/admin-boot.git
cd admin-boot
```

### 2. Maven 配置

确保 Maven 配置了阿里云镜像（国内加速）：

创建/编辑 `~/.m2/settings.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                              http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <mirrors>
        <mirror>
            <id>aliyun-maven</id>
            <name>Aliyun Maven</name>
            <url>https://maven.aliyun.com/repository/public</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>

    <profiles>
        <profile>
            <id>jdk-17</id>
            <activation>
                <activeByDefault>true</activeByDefault>
                <jdk>17</jdk>
            </activation>
            <properties>
                <maven.compiler.source>17</maven.compiler.source>
                <maven.compiler.target>17</maven.compiler.target>
                <maven.compiler.compilerVersion>17</maven.compiler.compilerVersion>
            </properties>
        </profile>
    </profiles>
</settings>
```

---

## 数据库初始化

### 1. 创建数据库

```bash
mysql -u root -p -e "CREATE DATABASE admin_boot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
```

### 2. 执行初始化脚本

```bash
mysql -u root -p admin_boot < resources/sql/V1__init.sql
```

### 3. 验证数据

```bash
mysql -u root -p admin_boot -e "SHOW TABLES;"
```

应该看到以下表：

```
sys_user
sys_role
sys_menu
sys_user_role
sys_role_menu
sys_dict
sys_dict_data
sys_config
sys_file
sys_logininfor
sys_oper_log
```

---

## 配置修改

### 1. 创建本地配置

在 `resources/` 目录下创建 `application-local.yml`：

```yaml
# 本地开发配置
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/admin_boot?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: root
    password: root123
  data:
    redis:
      host: localhost
      port: 6379
      password:
      database: 0

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # 打印 SQL

jwt:
  secret: dev-secret-key-for-local-development-only-change-in-production

knife4j:
  enable: true
```

### 2. 配置说明

| 配置项 | 说明 |
|--------|------|
| `spring.datasource.url` | MySQL 连接地址 |
| `spring.datasource.username` | 数据库用户名 |
| `spring.datasource.password` | 数据库密码 |
| `spring.data.redis.host` | Redis 地址 |
| `jwt.secret` | JWT 密钥（开发环境可简化） |

---

## 启动项目

### 使用 Maven 启动

```bash
# 开发环境启动
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 或指定主类
mvn spring-boot:run -Dspring-boot.run.mainClass=com.admin.AdminBootApplication
```

### 使用 IDE 启动

**IntelliJ IDEA**：

1. File → Open → 选择项目根目录
2. 等待 Maven 索引完成
3. 找到 `AdminBootApplication.java`
4. 右键 → Run 'AdminBootApplication'
5. 修改 Run Configuration，添加：
   - VM options: `-Dspring.profiles.active=local`
   - Working directory: 项目根目录

**VS Code**：

1. 安装 Extension Pack for Java
2. 安装 Spring Boot Extension Pack
3. 打开项目，等待索引
4. 按 F5 或点击 Run 按钮

### 验证启动

访问以下地址：

| 地址 | 说明 |
|------|------|
| http://localhost:8080 | 应用首页 |
| http://localhost:8080/doc.html | Knife4j API 文档 |
| http://localhost:8080/swagger-ui/index.html | Swagger UI |

登录测试：

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## 开发流程

### 开发新模块示例：产品管理

#### 1. 设计数据库表

```sql
-- 产品表
CREATE TABLE sys_product (
    id              BIGINT          NOT NULL   PRIMARY KEY AUTO_INCREMENT,
    product_name    VARCHAR(100)    NOT NULL   COMMENT '产品名称',
    product_code    VARCHAR(50)     NOT NULL   COMMENT '产品编码',
    price           DECIMAL(10,2)   NOT NULL   DEFAULT 0 COMMENT '价格',
    stock           INT             NOT NULL   DEFAULT 0 COMMENT '库存',
    description     VARCHAR(500)               COMMENT '产品描述',
    image_url       VARCHAR(255)               COMMENT '产品图片',
    status          TINYINT        DEFAULT 1   COMMENT '状态(0=下架,1=上架)',
    del_flag        TINYINT        DEFAULT 0   COMMENT '删除标志',
    create_by       VARCHAR(64)                COMMENT '创建者',
    create_time     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)                COMMENT '更新者',
    update_time     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)               COMMENT '备注',
    UNIQUE KEY uk_product_code (product_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';

CREATE INDEX idx_product_status ON sys_product(status);
CREATE INDEX idx_product_create_time ON sys_product(create_time);
```

#### 2. 使用代码生成器

修改 `admin-generator/src/main/resources/generator.yml`：

```yaml
# 代码生成器配置
author: developer
packageName: com.admin.system
autoRemovePre: false
tablePrefix: sys_

# 数据库配置
jdbc:
  driver: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://localhost:3306/admin_boot?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
  username: root
  password: root123

# 生成配置
generate:
  tableNames: sys_product
  outputDir: ../admin-system/src/main/java
```

运行生成器：

```bash
cd admin-generator
mvn spring-boot:run
```

或在 IDE 中运行 `GeneratorApplication.java`

#### 3. 生成的代码

```
admin-system/src/main/java/com/admin/
├── controller/
│   └── SysProductController.java
├── service/
│   ├── ISysProductService.java
│   └── impl/
│       └── SysProductServiceImpl.java
├── mapper/
│   └── SysProductMapper.java
├── entity/
│   └── SysProduct.java
├── dto/
│   ├── AddSysProductDTO.java
│   └── UpdateSysProductDTO.java
├── query/
│   └── SysProductQuery.java
├── vo/
│   └── SysProductVO.java
└── converter/
    └── SysProductConverter.java
```

#### 4. 添加菜单

在 `sys_menu` 表中插入菜单：

```sql
-- 插入产品管理目录
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, icon, create_time)
VALUES ('产品管理', 0, 5, 'product', NULL, 'M', 0, 1, 'product', NOW());

-- 插入产品列表菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_time)
VALUES (
    '产品列表',
    (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = '产品管理') t),
    1,
    'product',
    'system/product/index',
    'C',
    0,
    1,
    'product:product:list',
    'list',
    NOW()
);

-- 插入按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, menu_type, visible, status, perms, icon, create_time)
VALUES
('产品查询', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = '产品列表') t), 1, 'F', 0, 1, 'product:product:query', '#', NOW()),
('产品新增', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = '产品列表') t), 2, 'F', 0, 1, 'product:product:add', '#', NOW()),
('产品修改', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = '产品列表') t), 3, 'F', 0, 1, 'product:product:edit', '#', NOW()),
('产品删除', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = '产品列表') t), 4, 'F', 0, 1, 'product:product:remove', '#', NOW()),
('产品导出', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = '产品列表') t), 5, 'F', 0, 1, 'product:product:export', '#', NOW());
```

#### 5. 给角色分配权限

```sql
-- 给超级管理员分配产品管理权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'admin'
AND m.menu_name IN ('产品管理', '产品列表', '产品查询', '产品新增', '产品修改', '产品删除', '产品导出');
```

#### 6. 测试接口

重启应用，访问 http://localhost:8080/doc.html 测试接口。

---

## 常用命令

### Maven 命令

```bash
# 编译项目
mvn clean compile

# 打包项目
mvn clean package -DskipTests

# 运行测试
mvn test

# 跳过测试打包
mvn clean package -DskipTests

# 清理
mvn clean

# 仅安装依赖
mvn dependency:resolve

# 查看依赖树
mvn dependency:tree

# 跳过测试快速启动
mvn spring-boot:run -Dspring-boot.run.profiles=local -DskipTests
```

### 数据库命令

```bash
# 登录 MySQL
mysql -u root -p

# 执行 SQL 文件
mysql -u root -p admin_boot < init.sql

# 导出数据库
mysqldump -u root -p admin_boot > backup.sql

# 导入数据库
mysql -u root -p admin_boot < backup.sql
```

### Redis 命令

```bash
# 启动 Redis
redis-server

# 连接 Redis
redis-cli

# 常用命令
KEYS *           # 查看所有键
FLUSHDB          # 清空当前数据库
FLUSHALL         # 清空所有数据库
```

---

## IDE 配置

### IntelliJ IDEA

#### 插件安装

- Lombok
- MyBatisX
- Maven Helper
- Rainbow Brackets

#### 配置

1. **设置 JDK**：
   File → Project Structure → Project → SDK → 选择 JDK 17

2. **设置 Maven**：
   File → Settings → Build, Execution, Deployment → Build Tools → Maven
   - Maven home path: 选择 Maven 安装目录
   - settings.xml: 选择 `~/.m2/settings.xml`

3. **开启注解处理器**：
   File → Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   - Enable annotation processing: ✓

4. **设置代码风格**：
   File → Settings → Editor → Code Style → Java
   - 导入 Google/Java 代码风格文件

### VS Code

#### 扩展安装

- Extension Pack for Java
- Spring Boot Extension Pack
- MybatisX
- Lombok Annotations Support

#### settings.json 配置

```json
{
    "java.configuration.runtimes": [
        {
            "name": "JavaSE-17",
            "path": "/path/to/jdk-17",
            "default": true
        }
    ],
    "maven.executable.path": "/path/to/mvn",
    "spring-boot.ls.extension.enabled": true
}
```

---

## 调试技巧

### 1. 查看 SQL 日志

在 `application-local.yml` 中添加：

```yaml
mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
```

### 2. 断点调试

在 IDE 中：
1. 在需要调试的代码行左侧点击设置断点
2. 以 Debug 模式启动应用
3. 调用接口触发断点

### 3. Redis 调试

```bash
# 查看所有 token
redis-cli KEYS "jwt:*"

# 删除指定 token
redis-cli DEL "jwt:access:user_id"

# 清空所有 token
redis-cli FLUSHDB
```

### 4. 常用日志级别

```yaml
logging:
  level:
    root: INFO
    com.admin: DEBUG
    com.admin.mapper: DEBUG  # 打印 SQL
```

### 5. 热加载

开发时使用 Spring Boot DevTools：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <optional>true</optional>
</dependency>
```

修改代码后，IDE 编译自动重启应用。

---

## 常见问题

### 1. Maven 下载依赖慢

配置阿里云镜像，见上方 [Maven 配置](#2-maven-配置)

### 2. JDK 版本不对

确保使用 JDK 17+，检查：

```bash
java -version
echo $JAVA_HOME
```

### 3. 数据库连接失败

1. 检查 MySQL 是否运行
2. 检查端口是否正确（默认 3306）
3. 检查用户名密码
4. 检查数据库是否存在

### 4. Redis 连接失败

1. 检查 Redis 是否运行
2. 检查端口是否正确（默认 6379）
3. 检查密码是否正确（如果没有密码，保持空）

### 5. 端口被占用

```bash
# Windows 查看端口占用
netstat -ano | findstr 8080

# 结束进程
taskkill /PID <进程ID> /F
```

---

## 扩展阅读

- [架构设计文档](architecture-design.md)
- [部署指南](DEPLOY.md)
- [MyBatis-Plus 官方文档](https://baomidou.com/pages/24112f/)
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
