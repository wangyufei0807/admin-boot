# Admin-Boot 快速开发脚手架 - 详细设计文档

## 一、项目概述

### 1.1 项目简介
基于 SpringBoot + MyBatis-Plus 的快速开发脚手架，提供完整的 RBAC 权限管理功能，支持对接 Vue/R React 前端，开箱即用。

### 1.2 定位与目标
- **项目定位**：单体架构，按模块划分的后台管理系统
- **目标用户**：需要快速搭建后台管理系统的开发团队
- **核心能力**：用户管理、角色管理、菜单管理、字典管理、系统配置、操作日志

### 1.3 技术栈
| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 3.2.5 | 核心框架 |
| MyBatis-Plus | 3.5.7 | ORM框架 |
| Spring Security Crypto | 6.2.x | 密码加密模块 |
| jjwt | 0.12.5 | Token认证（Java 17+） |
| Redis | 7.x | 缓存、Token存储 |
| MySQL | 8.0 | 数据库 |
| Knife4j | 4.5.0 | API文档 |
| Hutool | 5.8.28 | 工具集 |
| Lombok | 1.18.32 | 简化代码 |

---

## 二、项目结构

### 2.1 模块划分原则

本项目采用 **Maven 多模块结构**，遵循以下原则：

| 原则 | 说明 |
|------|------|
| **单一职责** | 每个模块只负责一个领域功能 |
| **依赖单向** | 业务模块依赖公共模块，禁止反向依赖 |
| **测试内聚** | 测试代码放在各模块的 `src/test` 目录下 |
| **最小依赖** | 只引入必需的依赖，避免传递依赖 |

### 2.2 模块说明

| 模块 | 类型 | 职责 | 可独立运行 |
|------|------|------|------------|
| admin-boot | 父模块 | 统一版本管理、定义子模块 | ❌ |
| admin-common | 公共模块 | 公共组件、工具类、配置、注解、异常、安全 | ❌ |
| admin-system | 业务模块 | 业务代码（Controller、Service、Mapper、Entity） | ❌ |
| admin-generator | 工具模块 | 代码生成器 | ✅ |

### 2.3 项目结构

```
admin-boot
├── pom.xml                      # 父POM，统一管理依赖版本
│
├── admin-common                  # 公共模块
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/admin/common/
│       │   ├── annotation/       # 自定义注解（@Log、@RequiresPermissions）
│       │   ├── aspect/           # AOP切面（日志切面、权限切面）
│       │   ├── config/           # 配置类（Redis、Swagger、Security等）
│       │   ├── constant/         # 常量定义
│       │   ├── context/          # 上下文（UserContext等）
│       │   ├── enums/            # 枚举类
│       │   ├── exception/        # 异常定义与全局异常处理
│       │   ├── handler/          # 处理器（ResponseAdvice等）
│       │   ├── result/           # 统一响应封装（R、PageResult）
│       │   ├── security/         # 安全相关（JWT工具、Filter、加密）
│       │   └── utils/            # 工具类
│       └── test/                 # 公共模块单元测试
│           └── java/com/admin/common/
│
├── admin-system                  # 系统模块（业务代码）
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/admin/
│       │   └── {module}/        # 业务模块包（user/、role/、menu/...）
│       │       ├── controller/   # 控制器
│       │       ├── service/      # 服务层
│       │       │   ├── I{Module}Service.java
│       │       │   └── impl/
│       │       │       └── {Module}ServiceImpl.java
│       │       ├── mapper/       # 数据层
│       │       ├── entity/        # 实体类（DO）
│       │       ├── dto/           # 请求参数（新增/修改）
│       │       ├── query/         # 查询参数
│       │       ├── vo/            # 响应视图对象
│       │       └── converter/     # 对象转换器（Entity↔DTO/VO）
│       └── test/                  # 业务模块单元测试
│           └── java/com/admin/
│
├── admin-generator               # 代码生成器（独立运行）
│   ├── pom.xml
│   └── src/main/java/com/admin/generator/
│       └── GeneratorApplication.java
│
└── resources/                    # 资源文件（根目录）
    ├── application.yml            # 主配置文件
    ├── sql/                      # 数据库脚本
    └── mapper/                   # MyBatis XML
```

### 2.4 资源文件结构

```
resources/
├── application.yml               # 主配置文件
├── application-dev.yml            # 开发环境配置
├── application-prod.yml          # 生产环境配置
├── sql/
│   └── V1__init.sql              # Flyway 数据库迁移脚本
└── mapper/
    └── {module}/                 # MyBatis XML 文件
        ├── sys_user.xml
        └── sys_role.xml
```

### 2.5 新模块创建流程

当需要新增业务模块时，按以下步骤创建：

```bash
# 1. 在 admin-system 中创建模块包
MODULE_NAME="order"  # 示例：订单模块

mkdir -p src/main/java/com/admin/${MODULE_NAME}/controller
mkdir -p src/main/java/com/admin/${MODULE_NAME}/service/impl
mkdir -p src/main/java/com/admin/${MODULE_NAME}/mapper
mkdir -p src/main/java/com/admin/${MODULE_NAME}/entity
mkdir -p src/main/java/com/admin/${MODULE_NAME}/dto
mkdir -p src/main/java/com/admin/${MODULE_NAME}/query
mkdir -p src/main/java/com/admin/${MODULE_NAME}/vo
mkdir -p src/main/java/com/admin/${MODULE_NAME}/converter
mkdir -p src/main/resources/mapper/${MODULE_NAME}
```

**文件命名规范**：

| 类型 | 命名规则 | 示例 |
|------|----------|------|
| Controller | `{Module}Controller` | `OrderController` |
| Service接口 | `I{Module}Service` | `IOrderService` |
| Service实现 | `{Module}ServiceImpl` | `OrderServiceImpl` |
| Mapper | `{Module}Mapper` | `OrderMapper` |
| Entity | `{Module}` | `Order` |
| 新增DTO | `Add{Module}DTO` | `AddOrderDTO` |
| 修改DTO | `Update{Module}DTO` | `UpdateOrderDTO` |
| 查询参数 | `{Module}Query` | `OrderQuery` |
| 视图对象 | `{Module}VO` | `OrderVO` |
| Converter | `{Module}Converter` | `OrderConverter` |

---

## 三、数据库设计

### 3.1 设计原则

本项目采用 **共用数据库** 方案，所有业务表存储在同一个数据库中：

| 原则 | 说明 |
|------|------|
| **共用库** | 所有模块表放在同一个数据库中，便于关联查询和权限控制 |
| **表名前缀** | 按业务域添加前缀，如 `sys_`（系统）、`ord_`（订单）、`prod_`（产品） |
| **逻辑删除** | 所有业务表使用 `del_flag` 字段实现软删除 |
| **统一审计** | 所有表包含 `create_by`、`create_time`、`update_by`、`update_time` 审计字段 |

**为什么不分库？**
- 单体脚手架追求快速开发，分库增加运维复杂度
- RBAC 权限系统需要跨模块关联（用户→角色→菜单）
- 初期数据量小，共用库性能足够

**何时考虑分库？**
- 单表数据量超过千万级别
- 不同业务模块需要独立部署
- 需要严格的数据库权限隔离

### 3.2 模块表前缀规范

| 业务域 | 表前缀 | 示例 |
|--------|--------|------|
| 系统管理 | `sys_` | `sys_user`, `sys_role`, `sys_menu` |
| 订单管理 | `ord_` | `ord_order`, `ord_order_item` |
| 产品管理 | `prod_` | `prod_product`, `prod_category` |
| 会员管理 | `mem_` | `mem_member`, `mem_address` |

### 3.3 连表查询处理

在多模块架构下，连表查询根据场景选择不同处理方式：

#### 3.3.1 直接写 SQL（推荐，简单场景）

适用于同模块内或跨模块的简单关联查询，直接在 Mapper XML 中编写 JOIN 语句。

```xml
<!-- admin-system/src/resources/mapper/user.xml -->
<select id="selectUserWithRoles" resultType="UserVO">
    SELECT u.id, u.username, u.nick_name, u.email,
           GROUP_CONCAT(r.role_name) AS role_names
    FROM sys_user u
    LEFT JOIN sys_user_role ur ON u.id = ur.user_id
    LEFT JOIN sys_role r ON ur.role_id = r.id
    WHERE u.del_flag = 0 AND u.id = #{id}
    GROUP BY u.id
</select>
```

#### 3.3.2 Service 层组合（复杂场景）

适用于涉及多个业务域的复杂查询，在 Service 层组合调用多个 Mapper。

```java
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderMapper orderMapper;
    private final ProductService productService;

    @Override
    public OrderDetailVO getOrderDetail(Long orderId) {
        // 获取订单基本信息
        Order order = orderMapper.selectById(orderId);

        // 获取订单商品（跨模块调用）
        List<OrderItemVO> items = productService.getOrderItems(orderId);

        // 组装结果
        OrderDetailVO detail = new OrderDetailVO();
        detail.setOrder(order);
        detail.setItems(items);
        return detail;
    }
}
```

#### 3.3.3 MyBatis-Plus 关联查询（特定场景）

适用于一对多、多对一等固定关联关系，使用 MyBatis-Plus 的关联查询注解。

```java
// 方式一：使用 @One @Many 注解（需配置分页插件）
public class SysUser {
    @One(select = "com.admin.mapper.RoleMapper.selectRolesByUserId")
    private List<SysRole> roles;
}

// 方式二：在查询后手动处理
@Service
public class SysUserServiceImpl {

    public List<UserVO> listWithRoles(UserQuery query) {
        List<SysUser> users = userMapper.selectPage(query);
        return users.stream()
            .map(user -> {
                UserVO vo = toVO(user);
                vo.setRoles(roleService.getByUserId(user.getId()));
                return vo;
            })
            .collect(Collectors.toList());
    }
}
```

#### 3.3.4 查询方案选择

| 场景 | 推荐方案 | 说明 |
|------|----------|------|
| 单表查询 | MyBatis-Plus CRUD | 最简洁 |
| 两表简单关联 | Mapper XML JOIN | 性能好，一次查询 |
| 多表复杂关联 | Mapper XML JOIN | SQL 更清晰 |
| 跨模块业务组装 | Service 层组合 | 职责清晰，便于维护 |
| 一对多列表查询 | Service + 批量查询 | N+1 问题可接受时使用 |

**核心原则**：不要为了分层而分层，简单场景直接 SQL 更清晰。

### 3.4 ER图

```
┌──────────┐       ┌───────────────┐       ┌──────────┐
│ sys_user │──────▶│ sys_user_role │◀──────│ sys_role │
└──────────┘       └───────────────┘       └─────┬────┘
                                                  │
                                                  ▼
                    ┌───────────────┐       ┌──────────┐
                    │ sys_role_menu │◀──────│ sys_menu │
                    └───────────────┘       └──────────┘

┌──────────┐       ┌─────────────────┐
│ sys_dict │──────▶│ sys_dict_data  │
└──────────┘       └─────────────────┘

┌──────────────────┐    ┌─────────────────────┐
│  sys_logininfor │    │  sys_oper_log     │
└──────────────────┘    └─────────────────────┘

┌────────────┐    ┌────────────┐
│ sys_config │    │  sys_file │
└────────────┘    └────────────┘
```

### 3.5 表结构 DDL

```sql
-- ============================================
-- 系统管理相关表
-- ============================================

-- 用户表
CREATE TABLE sys_user (
    id              BIGINT          NOT NULL   COMMENT '主键ID'
        PRIMARY KEY
        AUTO_INCREMENT,
    username        VARCHAR(50)     NOT NULL   COMMENT '用户名'
        UNIQUE,
    password        VARCHAR(200)    NOT NULL   COMMENT '密码(BCrypt加密)',
    nick_name       VARCHAR(50)                COMMENT '昵称',
    email           VARCHAR(100)               COMMENT '邮箱',
    phone           VARCHAR(20)                COMMENT '手机号',
    avatar          VARCHAR(255)               COMMENT '头像',
    sex             TINYINT        DEFAULT 0   COMMENT '性别(0=未知,1=男,2=女)',
    status          TINYINT        DEFAULT 1   COMMENT '状态(0=禁用,1=正常)',
    del_flag        TINYINT        DEFAULT 0   COMMENT '删除标志(0=未删,1=已删)',
    login_ip        VARCHAR(45)                COMMENT '最后登录IP',
    login_time      DATETIME                   COMMENT '最后登录时间',
    create_by       VARCHAR(64)                COMMENT '创建者',
    create_time     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)                COMMENT '更新者',
    update_time     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)               COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户表索引
CREATE INDEX idx_user_status_del ON sys_user(status, del_flag);
CREATE INDEX idx_user_phone ON sys_user(phone);
CREATE INDEX idx_user_create_time ON sys_user(create_time);

-- 角色表
CREATE TABLE sys_role (
    id              BIGINT          NOT NULL   COMMENT '主键ID'
        PRIMARY KEY
        AUTO_INCREMENT,
    role_name       VARCHAR(50)     NOT NULL   COMMENT '角色名称',
    role_key        VARCHAR(100)    NOT NULL   COMMENT '角色标识',
    role_sort       INT             NOT NULL   COMMENT '显示顺序',
    menu_check_strictly TINYINT    DEFAULT 1   COMMENT '菜单树选择组件是否严格级联',
    status          TINYINT        DEFAULT 1   COMMENT '状态(0=禁用,1=正常)',
    del_flag        TINYINT        DEFAULT 0   COMMENT '删除标志(0=未删,1=已删)',
    create_by       VARCHAR(64)                COMMENT '创建者',
    create_time     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)                COMMENT '更新者',
    update_time     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)               COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 角色表索引
CREATE INDEX idx_role_status_del ON sys_role(status, del_flag);

-- 菜单权限表
CREATE TABLE sys_menu (
    id              BIGINT          NOT NULL   COMMENT '主键ID'
        PRIMARY KEY
        AUTO_INCREMENT,
    menu_name       VARCHAR(50)     NOT NULL   COMMENT '菜单名称',
    parent_id       BIGINT         DEFAULT 0   COMMENT '父菜单ID',
    order_num       INT             NOT NULL   COMMENT '显示顺序',
    path            VARCHAR(200)                COMMENT '路由地址',
    component       VARCHAR(255)               COMMENT '组件路径',
    query_param     VARCHAR(255)               COMMENT '路由参数',
    is_frame        TINYINT        DEFAULT 1   COMMENT '是否为外链(0=是,1=否)',
    is_cache        TINYINT        DEFAULT 0   COMMENT '是否缓存(0=缓存,1=不缓存)',
    menu_type       CHAR(1)                    COMMENT '菜单类型(M=目录,C=菜单,F=按钮)',
    visible         TINYINT        DEFAULT 0   COMMENT '菜单状态(0=显示,1=隐藏)',
    status          TINYINT        DEFAULT 1   COMMENT '菜单状态(0=禁用,1=正常)',
    perms           VARCHAR(100)               COMMENT '权限标识',
    icon            VARCHAR(100)               COMMENT '菜单图标',
    create_by       VARCHAR(64)                COMMENT '创建者',
    create_time     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)                COMMENT '更新者',
    update_time     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)               COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单权限表';

-- 菜单表索引
CREATE INDEX idx_menu_parent ON sys_menu(parent_id);
CREATE INDEX idx_menu_type_status ON sys_menu(menu_type, status);

-- 用户和角色关联表
CREATE TABLE sys_user_role (
    user_id         BIGINT          NOT NULL   COMMENT '用户ID',
    role_id         BIGINT          NOT NULL   COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户和角色关联表';

-- 角色和菜单关联表
CREATE TABLE sys_role_menu (
    role_id         BIGINT          NOT NULL   COMMENT '角色ID',
    menu_id         BIGINT          NOT NULL   COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色和菜单关联表';

-- ============================================
-- 字典相关表
-- ============================================

-- 字典类型表
CREATE TABLE sys_dict (
    id              BIGINT          NOT NULL   COMMENT '主键ID'
        PRIMARY KEY
        AUTO_INCREMENT,
    dict_name       VARCHAR(100)    NOT NULL   COMMENT '字典名称',
    dict_type       VARCHAR(100)    NOT NULL   COMMENT '字典类型'
        UNIQUE,
    status          TINYINT        DEFAULT 1   COMMENT '状态(0=禁用,1=正常)',
    create_by       VARCHAR(64)                COMMENT '创建者',
    create_time     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)                COMMENT '更新者',
    update_time     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)               COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典类型表';

-- 字典类型表索引
CREATE INDEX idx_dict_type ON sys_dict(dict_type);

-- 字典数据表
CREATE TABLE sys_dict_data (
    id              BIGINT          NOT NULL   COMMENT '主键ID'
        PRIMARY KEY
        AUTO_INCREMENT,
    dict_sort       INT                         COMMENT '字典排序',
    dict_label      VARCHAR(100)                COMMENT '字典标签',
    dict_value      VARCHAR(100)                COMMENT '字典键值',
    dict_type       VARCHAR(100)    NOT NULL   COMMENT '字典类型',
    css_class       VARCHAR(100)               COMMENT '样式属性',
    list_class      VARCHAR(100)               COMMENT '表格回显样式',
    is_default      TINYINT        DEFAULT 0   COMMENT '是否默认(0=否,1=是)',
    status          TINYINT        DEFAULT 1   COMMENT '状态(0=禁用,1=正常)',
    create_by       VARCHAR(64)                COMMENT '创建者',
    create_time     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)                COMMENT '更新者',
    update_time     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)               COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='字典数据表';

-- 字典数据表索引
CREATE INDEX idx_dict_data_type ON sys_dict_data(dict_type);
CREATE INDEX idx_dict_data_status ON sys_dict_data(status);

-- ============================================
-- 日志相关表
-- ============================================

-- 系统访问记录表
CREATE TABLE sys_logininfor (
    id              BIGINT          NOT NULL   COMMENT '访问ID'
        PRIMARY KEY
        AUTO_INCREMENT,
    user_id         BIGINT                     COMMENT '用户ID',
    user_name       VARCHAR(50)                COMMENT '用户名称',
    ipaddr          VARCHAR(45)                COMMENT '登录IP地址(支持IPv6)',
    login_location  VARCHAR(255)               COMMENT '登录地点',
    browser         VARCHAR(50)                COMMENT '浏览器类型',
    os              VARCHAR(50)                COMMENT '操作系统',
    login_status    TINYINT                    COMMENT '登录状态(0=成功,1=失败)',
    msg             VARCHAR(255)               COMMENT '提示消息',
    login_time      DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统访问记录表';

-- 登录日志表索引
CREATE INDEX idx_login_user ON sys_logininfor(user_id);
CREATE INDEX idx_login_status ON sys_logininfor(login_status);
CREATE INDEX idx_login_time ON sys_logininfor(login_time);

-- 操作日志记录表
CREATE TABLE sys_oper_log (
    id              BIGINT          NOT NULL   COMMENT '日志主键'
        PRIMARY KEY
        AUTO_INCREMENT,
    title           VARCHAR(50)                COMMENT '模块标题',
    business_type   INT                         COMMENT '业务类型(0=其他,1=新增,2=修改,3=删除...)',
    method          VARCHAR(100)                COMMENT '请求方法',
    request_method  VARCHAR(10)                 COMMENT '请求方式',
    operator_type   TINYINT                    COMMENT '操作类别(0=后台用户,1=手机端用户)',
    user_id         BIGINT                     COMMENT '用户ID',
    user_name       VARCHAR(50)                 COMMENT '用户名',
    url             VARCHAR(255)               COMMENT '请求URL',
    ipaddr          VARCHAR(45)                COMMENT 'IP地址',
    oper_location   VARCHAR(255)               COMMENT '操作地点',
    oper_param      VARCHAR(2000)              COMMENT '请求参数',
    json_result     VARCHAR(2000)              COMMENT '返回参数',
    oper_status     INT             DEFAULT 0  COMMENT '操作状态(0=正常,1=异常)',
    error_msg       VARCHAR(2000)              COMMENT '错误消息',
    oper_time       DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    cost_time       BIGINT                     COMMENT '消耗时间(ms)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志记录表';

-- 操作日志表索引
CREATE INDEX idx_oper_user ON sys_oper_log(user_id);
CREATE INDEX idx_oper_status ON sys_oper_log(oper_status);
CREATE INDEX idx_oper_time ON sys_oper_log(oper_time);

-- ============================================
-- 配置相关表
-- ============================================

-- 参数配置表
CREATE TABLE sys_config (
    id              BIGINT          NOT NULL   COMMENT '主键ID'
        PRIMARY KEY
        AUTO_INCREMENT,
    config_name     VARCHAR(100)    NOT NULL   COMMENT '配置名称',
    config_key      VARCHAR(100)    NOT NULL   COMMENT '配置key',
    config_value    VARCHAR(500)    NOT NULL   COMMENT '配置值',
    config_type     CHAR(1)         DEFAULT 'N' COMMENT '系统内置(Y=是,N=否)',
    create_by       VARCHAR(64)                COMMENT '创建者',
    create_time     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_by       VARCHAR(64)                COMMENT '更新者',
    update_time     DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark          VARCHAR(500)               COMMENT '备注'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='参数配置表';

-- 参数配置表索引
CREATE UNIQUE INDEX idx_config_key ON sys_config(config_key);

-- ============================================
-- 文件表
-- ============================================

CREATE TABLE sys_file (
    id              BIGINT          NOT NULL   COMMENT '主键ID'
        PRIMARY KEY
        AUTO_INCREMENT,
    file_name       VARCHAR(255)    NOT NULL   COMMENT '文件名称',
    file_path       VARCHAR(500)    NOT NULL   COMMENT '文件路径',
    file_size       BIGINT                     COMMENT '文件大小(字节)',
    file_type       VARCHAR(50)                COMMENT '文件类型(MIME)',
    storage_type    VARCHAR(20)    DEFAULT 'local' COMMENT '存储类型(local/oss/s3)',
    create_by       VARCHAR(64)                COMMENT '创建者',
    create_time     DATETIME       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件表';

-- 文件表索引
CREATE INDEX idx_file_create_time ON sys_file(create_time);
CREATE INDEX idx_file_storage_type ON sys_file(storage_type);
```

---

## 四、API接口规范

### 4.1 统一响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": { ... }
}
```

### 4.2 响应码定义

| 范围 | 说明 |
|------|------|
| 1xx | 参数/请求错误 |
| 2xx | 业务逻辑错误 |
| 4xx | 认证/权限错误 |
| 5xx | 系统异常 |

### 4.3 响应码设计

本项目采用 **简化状态码** 设计，HTTP 状态码与业务码统一，只保留必要的区分：

| 状态码 | 说明 | 使用场景 |
|--------|------|----------|
| 200 | 成功 | 所有正常返回 |
| 400 | 请求/业务错误 | 参数错误、数据不存在、业务规则不满足 |
| 401 | 未认证 | 未登录、Token 无效或过期 |
| 403 | 无权限 | 无访问权限 |
| 500 | 系统异常 | 系统错误、数据库异常等 |

**响应格式**：
```json
// 成功
{ "code": 200, "message": "success", "data": {...} }

// 失败 - 通过 message 说明具体原因
{ "code": 400, "message": "用户不存在" }
{ "code": 400, "message": "订单状态不允许此操作" }
{ "code": 401, "message": "登录已过期，请重新登录" }
{ "code": 403, "message": "无权限访问" }
{ "code": 500, "message": "系统异常" }
```

**枚举定义**：
```java
public enum ResponseCode {
    SUCCESS(200, "操作成功"),

    // 4xx 请求/业务错误（message 详细说明原因）
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无访问权限"),

    // 5xx 系统错误
    SYSTEM_ERROR(500, "系统异常"),
    DB_ERROR(500, "数据库异常"),
    REDIS_ERROR(500, "缓存服务异常"),
    FILE_ERROR(500, "文件操作异常");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
```

**设计原则**：
- 前端统一根据 HTTP 状态码大类处理
- 具体错误原因通过 `message` 字段展示给用户
- 业务模块无需定义新的状态码枚举，直接在 `message` 中说明

### 4.4 分页响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [...],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

### 4.5 认证相关接口

#### 4.5.1 登录
```
POST /api/auth/login
Content-Type: application/json

Request:
{
  "username": "admin",
  "password": "admin123"
}

Response:
{
  "code": 200,
  "message": "success",
  "data": {
    "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expires_in": 900,
    "token_type": "Bearer"
  }
}
```

#### 4.5.2 刷新Token
```
POST /api/auth/refresh
Content-Type: application/json

Request:
{
  "refresh_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### 4.5.3 登出
```
POST /api/auth/logout
Authorization: Bearer <access_token>
```

### 4.6 用户管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/user/list | 用户列表 |
| GET | /api/system/user/{id} | 用户详情 |
| POST | /api/system/user | 新增用户 |
| PUT | /api/system/user/{id} | 修改用户 |
| DELETE | /api/system/user/{id} | 删除用户 |
| DELETE | /api/system/user/batch/{ids} | 批量删除用户 |
| PUT | /api/system/user/{id}/profile | 修改个人信息 |
| PUT | /api/system/user/{id}/password | 修改密码 |
| PUT | /api/system/user/{id}/avatar | 修改头像 |
| PUT | /api/system/user/{id}/status | 修改用户状态 |

### 4.7 角色管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/role/list | 角色列表 |
| GET | /api/system/role/{id} | 角色详情 |
| POST | /api/system/role | 新增角色 |
| PUT | /api/system/role/{id} | 修改角色 |
| DELETE | /api/system/role/{id} | 删除角色 |
| DELETE | /api/system/role/batch/{ids} | 批量删除角色 |
| PUT | /api/system/role/{id}/status | 修改角色状态 |
| GET | /api/system/role/{id}/menu | 获取角色已分配菜单 |
| PUT | /api/system/role/{id}/menu | 分配菜单权限 |

### 4.8 菜单管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/menu/list | 菜单列表 |
| GET | /api/system/menu/{id} | 菜单详情 |
| POST | /api/system/menu | 新增菜单 |
| PUT | /api/system/menu/{id} | 修改菜单 |
| DELETE | /api/system/menu/{id} | 删除菜单 |
| GET | /api/system/menu/treeselect | 菜单下拉树 |
| GET | /api/system/menu/role/{roleId}/treeselect | 根据角色获取菜单下拉树 |

### 4.9 字典管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/dict/type/list | 字典类型列表 |
| GET | /api/system/dict/type/{id} | 字典类型详情 |
| POST | /api/system/dict/type | 新增字典类型 |
| PUT | /api/system/dict/type/{id} | 修改字典类型 |
| DELETE | /api/system/dict/type/{id} | 删除字典类型 |
| DELETE | /api/system/dict/type/batch/{ids} | 批量删除字典类型 |
| GET | /api/system/dict/data/{dictType} | 获取字典数据 |
| GET | /api/system/dict/data/list | 字典数据列表 |
| GET | /api/system/dict/data/{id} | 字典数据详情 |
| POST | /api/system/dict/data | 新增字典数据 |
| PUT | /api/system/dict/data/{id} | 修改字典数据 |
| DELETE | /api/system/dict/data/{id} | 删除字典数据 |

### 4.10 日志接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/login-log/list | 登录日志列表 |
| DELETE | /api/system/login-log/{id} | 删除登录日志 |
| DELETE | /api/system/login-log/batch/{ids} | 批量删除登录日志 |
| DELETE | /api/system/login-log/clean | 清空登录日志 |
| GET | /api/system/oper-log/list | 操作日志列表 |
| GET | /api/system/oper-log/{id} | 操作日志详情 |
| DELETE | /api/system/oper-log/{id} | 删除操作日志 |
| DELETE | /api/system/oper-log/batch/{ids} | 批量删除操作日志 |
| DELETE | /api/system/oper-log/clean | 清空操作日志 |

### 4.11 配置接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/system/config/list | 配置列表 |
| GET | /api/system/config/{id} | 配置详情 |
| POST | /api/system/config | 新增配置 |
| PUT | /api/system/config/{id} | 修改配置 |
| DELETE | /api/system/config/{id} | 删除配置 |
| DELETE | /api/system/config/batch/{ids} | 批量删除配置 |
| GET | /api/system/config/key/{configKey} | 根据key获取配置 |
| GET | /api/system/config/all | 获取所有配置 |

### 4.12 文件接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/system/file/upload | 上传文件 |
| GET | /api/system/file/{id} | 文件详情 |
| GET | /api/system/file/{id}/download | 下载文件 |
| DELETE | /api/system/file/{id} | 删除文件 |

---

## 五、核心代码规范

### 5.1 启动类配置

Spring Boot 应用程序启动类，通常放在项目根包的 `Application.java` 文件中：

**主启动类**：
```java
package com.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.admin.**.mapper")  // 扫描 Mapper 接口
public class AdminBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminBootApplication.class, args);
    }
}
```

**常用注解说明**：

| 注解 | 说明 |
|------|------|
| `@SpringBootApplication` | 组合注解，包含 `@Configuration`、`@EnableAutoConfiguration`、`@ComponentScan` |
| `@MapperScan` | 扫描 MyBatis Mapper 接口，路径支持通配符 |
| `@EnableConfigurationProperties` | 启用配置属性类（如 SecurityProperties、CorsProperties） |

**多环境启动**：
```bash
# 开发环境
java -jar admin-boot.jar --spring.profiles.active=dev

# 测试环境
java -jar admin-boot.jar --spring.profiles.active=test

# 生产环境
java -jar admin-boot.jar --spring.profiles.active=prod
```

**启动参数优化**（生产环境建议）：
```bash
java -jar admin-boot.jar \
    --spring.profiles.active=prod \
    -Xms512m -Xmx1024m \
    -XX:+UseG1GC \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath=/var/logs/heapdump.hprof
```

**启动检查清单**：
- [ ] MySQL 和 Redis 服务已启动
- [ ] 数据库已创建
- [ ] Flyway 迁移脚本存在
- [ ] 端口 8080 未被占用
- [ ] 日志目录可写

### 5.2 统一响应封装

```java
@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> R<T> ok() {
        return new R<>(ResponseCode.SUCCESS.getCode(),
                       ResponseCode.SUCCESS.getMessage(), null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResponseCode.SUCCESS.getCode(),
                       ResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(ResponseCode.SUCCESS.getCode(), message, data);
    }

    public static <T> R<T> fail(ResponseCode responseCode) {
        return new R<>(responseCode.getCode(), responseCode.getMessage(), null);
    }

    public static <T> R<T> fail(ResponseCode responseCode, String message) {
        return new R<>(responseCode.getCode(), message, null);
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }

    public boolean isSuccess() {
        return code == ResponseCode.SUCCESS.getCode();
    }
}
```

### 5.3 分页响应封装

```java
@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> records;
    private long total;
    private long size;
    private long current;
    private long pages;

    public PageResult() {}

    public PageResult(List<T> records, long total, IPage<?> page) {
        this.records = records;
        this.total = total;
        this.size = page.getSize();
        this.current = page.getCurrent();
        this.pages = page.getPages();
    }

    public static <T> PageResult<T> of(List<T> records, long total, IPage<?> page) {
        return new PageResult<>(records, total, page);
    }
}
```

### 5.4 基础实体类

```java
@Data
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String remark;
}
```

### 5.5 分页查询基类

```java
@Data
public class PageQuery {
    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public IPage<Page> toPage() {
        return toPage(new Page<>());
    }

    public <T> IPage<T> toPage(IPage<T> page) {
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        return page;
    }
}
```

### 5.6 基础Controller

```java
@RestController
@RequestMapping("/api/system/user")
@RequiredArgsConstructor
@Api(tags = "用户管理")
public class SysUserController {

    private final ISysUserService userService;

    @GetMapping("/list")
    @ApiOperation("用户列表")
    @RequiresPermissions("system:user:list")
    public R<PageResult<SysUserVO>> list(SysUserQuery query) {
        IPage<SysUserVO> page = userService.page(query);
        return R.ok(PageResult.of(page.getRecords(), page.getTotal(), page));
    }

    @GetMapping("/{id}")
    @ApiOperation("用户详情")
    @RequiresPermissions("system:user:query")
    public R<SysUserVO> get(@PathVariable Long id) {
        return R.ok(userService.getById(id));
    }

    @PostMapping
    @ApiOperation("新增用户")
    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    public R<Void> add(@RequestBody @Validated AddUserDTO dto) {
        userService.add(dto);
        return R.ok();
    }

    @PutMapping("/{id}")
    @ApiOperation("修改用户")
    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public R<Void> update(@PathVariable Long id,
                          @RequestBody @Validated UpdateUserDTO dto) {
        dto.setId(id);
        userService.update(dto);
        return R.ok();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除用户")
    @RequiresPermissions("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    public R<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return R.ok();
    }

    @DeleteMapping("/batch/{ids}")
    @ApiOperation("批量删除用户")
    @RequiresPermissions("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    public R<Void> batchDelete(@PathVariable List<Long> ids) {
        userService.deleteBatch(ids);
        return R.ok();
    }
}
```

### 5.6.1 Swagger API 文档注解规范

Knife4j（基于 Swagger）用于生成 API 文档，需要对所有 Entity、DTO、VO 添加字段说明注解。

**常用注解说明**：

| 注解 | 位置 | 说明 |
|------|------|------|
| `@Tag` | Controller 类 | API 分类标签 |
| `@Operation` | Controller 方法 | 接口描述（summary 属性） |
| `@ApiModel` | DTO/VO 类 | 模型描述 |
| `@ApiModelProperty` | DTO/VO 字段 | 字段说明、是否必填 |
| `@ApiParam` | Controller 参数 | 参数说明 |

**VO 类注解示例**：
```java
@Tag(name = "用户管理")
@Data
public class SysUserVO {

    @ApiModelProperty("用户ID")
    private Long id;

    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "头像URL")
    private String avatar;

    @ApiModelProperty(value = "性别", notes = "0=未知,1=男,2=女")
    private Integer sex;

    @ApiModelProperty(value = "状态", notes = "0=禁用,1=正常")
    private Integer status;

    @ApiModelProperty("最后登录IP")
    private String loginIp;

    @ApiModelProperty("最后登录时间")
    private LocalDateTime loginTime;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("角色ID列表")
    private List<Long> roleIds;

    @ApiModelProperty("角色名称列表")
    private List<String> roleNames;
}
```

**DTO 类注解示例**：
```java
@Tag(name = "用户管理")
@Data
public class AddUserDTO {

    @ApiModelProperty(value = "用户名", required = true, position = 1)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "密码", required = true, position = 2)
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度8-20位")
    private String password;

    @ApiModelProperty(value = "昵称", position = 3)
    private String nickName;

    @ApiModelProperty(value = "邮箱", position = 4)
    @Email(message = "邮箱格式不正确")
    private String email;

    @ApiModelProperty(value = "手机号", position = 5)
    private String phone;

    @ApiModelProperty(value = "性别", notes = "0=未知,1=男,2=女", position = 6)
    private Integer sex;

    @ApiModelProperty(value = "状态", notes = "0=禁用,1=正常", position = 7)
    private Integer status;

    @ApiModelProperty(value = "角色ID列表", position = 8)
    private List<Long> roleIds;

    @ApiModelProperty(value = "备注", position = 9)
    private String remark;
}
```

**UpdateDTO 类注解示例**：
```java
@Tag(name = "用户管理")
@Data
public class UpdateUserDTO {

    @ApiModelProperty(value = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long id;

    @ApiModelProperty(value = "用户名", required = true)
    @NotBlank(message = "用户名不能为空")
    private String username;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "性别", notes = "0=未知,1=男,2=女")
    private Integer sex;

    @ApiModelProperty(value = "状态", notes = "0=禁用,1=正常")
    private Integer status;

    @ApiModelProperty(value = "角色ID列表")
    private List<Long> roleIds;

    @ApiModelProperty(value = "备注")
    private String remark;
}
```

**Query 类注解示例**：
```java
@Tag(name = "用户管理")
@Data
public class SysUserQuery {

    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum = 1;

    @ApiModelProperty(value = "每页数量", example = "10")
    private Integer pageSize = 10;

    @ApiModelProperty(value = "用户名", example = "admin")
    private String username;

    @ApiModelProperty(value = "手机号", example = "13800138000")
    private String phone;

    @ApiModelProperty(value = "状态", notes = "0=禁用,1=正常", example = "1")
    private Integer status;

    @ApiModelProperty(value = "开始日期", example = "2024-01-01")
    private LocalDateTime startDate;

    @ApiModelProperty(value = "结束日期", example = "2024-12-31")
    private LocalDateTime endDate;
}
```

**Controller 参数注解示例**：
```java
@GetMapping("/{id}")
@Operation(summary = "用户详情")
@ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "Long", example = "1")
public R<SysUserVO> get(@PathVariable Long id) {
    return R.ok(userService.getById(id));
}

@PostMapping
@Operation(summary = "新增用户")
public R<Void> add(@RequestBody @Validated AddUserDTO dto) {
    userService.add(dto);
    return R.ok();
}

@DeleteMapping("/batch/{ids}")
@Operation(summary = "批量删除用户")
@ApiImplicitParams({
    @ApiImplicitParam(name = "ids", value = "用户ID列表", required = true, dataType = "Long[]", example = "[1,2,3]")
})
public R<Void> batchDelete(@PathVariable Long[] ids) {
    userService.deleteBatch(ids);
    return R.ok();
}
```

**注解属性说明**：

| 属性 | 适用注解 | 说明 | 示例 |
|------|---------|------|------|
| `value` | 所有 | 字段/参数描述 | `"用户名"` |
| `required` | `@ApiModelProperty` | 是否必填 | `true` |
| `example` | `@ApiModelProperty` | 示例值 | `"admin"` |
| `notes` | `@ApiModelProperty` | 额外说明 | `"0=禁用,1=正常"` |
| `position` | `@ApiModelProperty` | 表单/JSON顺序 | `1` |
| `hidden` | `@ApiModelProperty` | 是否隐藏 | `false` |
| `dataType` | `@ApiImplicitParam` | 数据类型 | `"Long"` |
| ` allowableValues` | `@ApiModelProperty` | 允许值 | `"0,1,2"` |

**设计原则**：
- 所有 VO、DTO、Query 类必须添加 `@Tag` 和 `@ApiModelProperty`
- 必填字段设置 `required = true`
- 枚举值字段设置 `notes` 说明可选值
- 分页参数设置 `example` 示例值
- 使用 `position` 控制表单/JSON字段顺序
- 不需要暴露的字段设置 `hidden = true`

### 5.7 基础Service

```java
public interface ISysUserService {
    IPage<SysUserVO> page(SysUserQuery query);
    SysUserVO getById(Long id);
    void add(AddUserDTO dto);
    void update(UpdateUserDTO dto);
    void delete(Long id);
    void deleteBatch(List<Long> ids);
}

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper userMapper;

    @Override
    public IPage<SysUserVO> page(SysUserQuery query) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(query.getUsername()),
                     SysUser::getUsername, query.getUsername())
               .eq(query.getStatus() != null,
                   SysUser::getStatus, query.getStatus())
               .eq(query.getPhone() != null,
                   SysUser::getPhone, query.getPhone())
               .orderByDesc(SysUser::getCreateTime);

        IPage<SysUser> page = userMapper.selectPage(query.toPage(), wrapper);
        return page.convert(SysUserConverter.INSTANCE::toVO);
    }

    @Override
    public SysUserVO getById(Long id) {
        SysUser user = userMapper.selectById(id);
        return SysUserConverter.INSTANCE.toVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(AddUserDTO dto) {
        // 校验用户名唯一
        checkUsernameUnique(dto.getUsername());
        // 校验手机号唯一
        checkPhoneUnique(dto.getPhone());

        SysUser user = SysUserConverter.INSTANCE.toEntity(dto);
        user.setPassword(passwordEncoder.encode("123456")); // 默认密码
        userMapper.insert(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UpdateUserDTO dto) {
        SysUser user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException(ResponseCode.BAD_REQUEST.getCode(), "用户不存在");
        }
        SysUserConverter.INSTANCE.updateEntity(dto, user);
        userMapper.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        userMapper.deleteBatchIds(ids);
    }
}
```

### 5.8 权限校验注解

```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequiresPermissions {
    /**
     * 权限标识
     */
    String[] value();

    /**
     * 权限组合方式
     */
    Logical logical() default Logical.AND;
}

public enum Logical {
    AND,  // 必须拥有所有权限
    OR    // 拥有任一权限即可
}
```

### 5.9 操作日志注解

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    /**
     * 模块名称
     */
    String title();

    /**
     * 业务类型
     */
    BusinessType businessType() default BusinessType.OTHER;

    /**
     * 是否保存请求参数
     */
    boolean saveParam() default true;

    /**
     * 是否保存返回参数
     */
    boolean saveResult() default false;
}

public enum BusinessType {
    OTHER(0, "其他"),
    INSERT(1, "新增"),
    UPDATE(2, "修改"),
    DELETE(3, "删除"),
    GRANT(4, "授权"),
    EXPORT(5, "导出"),
    IMPORT(6, "导入"),
    QUERY(7, "查询"),
    GEN_CODE(8, "生成代码"),
    CLEAN(9, "清空数据");

    private final int code;
    private final String desc;

    BusinessType(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
```

### 5.10 文件存储接口

```java
public interface FileStorageService {
    /**
     * 上传文件
     */
    String upload(MultipartFile file);

    /**
     * 上传文件（指定路径）
     */
    String upload(MultipartFile file, String path);

    /**
     * 删除文件
     */
    void delete(String path);

    /**
     * 获取文件访问URL
     */
    String getUrl(String path);

    /**
     * 下载文件
     */
    void download(String path, HttpServletResponse response);
}

// 本地存储实现
@Component
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {
    // ... 实现代码
}

// OSS存储实现
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "file.storage-type", havingValue = "oss")
public class OssFileStorageService implements FileStorageService {
    // ... 实现代码
}
```

### 5.11 异常类定义

```java
// 业务异常
@Data
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;

    public BusinessException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public BusinessException(ResponseCode responseCode, String message) {
        this.code = responseCode.getCode();
        this.message = message;
    }
}

// 系统异常
@Data
@AllArgsConstructor
public class SystemException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;

    public SystemException(String message) {
        super(message);
        this.code = ResponseCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.SYSTEM_ERROR.getCode();
        this.message = message;
    }
}
```

### 5.12 对象转换器（MapStruct）

使用 MapStruct 实现 Entity ↔ DTO/VO 的自动转换：

```java
// pom.xml 添加依赖
<!-- MapStruct -->
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
</dependency>
```

```java
@Mapper(componentModel = "spring")
public interface SysUserConverter {

    SysUserConverter INSTANCE = Mappers.getMapper(SysUserConverter.class);

    /**
     * Entity 转 VO
     */
    @Mapping(target = "createTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updateTime", dateFormat = "yyyy-MM-dd HH:mm:ss")
    SysUserVO toVO(SysUser entity);

    /**
     * DTO 转 Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    SysUser toEntity(AddUserDTO dto);

    /**
     * 更新 Entity（保留 id 和创建字段）
     */
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "delFlag", ignore = true)
    void updateEntity(UpdateUserDTO dto, @MappingTarget SysUser entity);
}
```

**使用示例**：

```java
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements ISysUserService {

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void add(AddUserDTO dto) {
        SysUser user = SysUserConverter.INSTANCE.toEntity(dto);
        user.setPassword(passwordEncoder.encode("123456"));
        userMapper.insert(user);
    }

    @Override
    public void update(UpdateUserDTO dto) {
        SysUser user = userMapper.selectById(dto.getId());
        SysUserConverter.INSTANCE.updateEntity(dto, user);
        userMapper.updateById(user);
    }
}
```

### 5.13 MyBatis-Plus 配置

```java
@Configuration
public class MybatisPlusConfig {

    /**
     * 分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 乐观锁插件（可选）
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }
}
```

### 5.14 自动填充处理器

```java
@Component
@RequiredArgsConstructor
public class MetaObjectHandlerImpl implements MetaObjectHandler {

    private final SysUserService userService;

    @Override
    public void insertFill(MetaObject metaObject) {
        // 获取当前登录用户
        String username = getCurrentUsername();
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createBy", String.class, username);
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateBy", String.class, username);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        String username = getCurrentUsername();
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, username);
    }

    /**
     * 获取当前登录用户名
     */
    private String getCurrentUsername() {
        try {
            LoginUser loginUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return loginUser != null ? loginUser.getUsername() : "system";
        } catch (Exception e) {
            return "system";
        }
    }
}
```

### 5.15 完整全局异常处理

```java
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(BusinessException e) {
        log.warn("Business exception: {}", e.getMessage());
        return R.fail(e.getCode(), e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(SystemException.class)
    public R<Void> handleSystemException(SystemException e) {
        log.error("System exception", e);
        return R.fail(ResponseCode.SYSTEM_ERROR.getCode(), e.getMessage());
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        log.warn("Validation exception: {}", message);
        return R.fail(ResponseCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 绑定异常
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数绑定失败";
        log.warn("Bind exception: {}", message);
        return R.fail(ResponseCode.BAD_REQUEST.getCode(), message);
    }

    /**
     * 缺少参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public R<Void> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        log.warn("Missing parameter: {}", e.getParameterName());
        return R.fail(ResponseCode.BAD_REQUEST.getCode(),
                "缺少参数: " + e.getParameterName());
    }

    /**
     * 权限不足异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public R<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.warn("Access denied: {}", e.getMessage());
        return R.fail(ResponseCode.FORBIDDEN.getCode(), "无访问权限");
    }

    /**
     * 认证异常
     */
    @ExceptionHandler(AuthenticationException.class)
    public R<Void> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication failed: {}", e.getMessage());
        return R.fail(ResponseCode.UNAUTHORIZED.getCode(), "未登录或登录已过期");
    }

    /**
     * SQL 异常
     */
    @ExceptionHandler(BadSqlGrammarException.class)
    public R<Void> handleBadSqlGrammarException(BadSqlGrammarException e) {
        log.error("SQL error", e);
        return R.fail(ResponseCode.DB_ERROR.getCode(), "数据库执行异常");
    }

    /**
     * 文件上传异常
     */
    @ExceptionHandler(FileUploadException.class)
    public R<Void> handleFileUploadException(FileUploadException e) {
        log.error("File upload error", e);
        return R.fail(ResponseCode.FILE_ERROR.getCode(), "文件上传失败");
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("Unexpected error", e);
        return R.fail(ResponseCode.SYSTEM_ERROR.getCode(), "系统异常，请联系管理员");
    }
}
```

### 5.16 登录相关类

```java
// 登录请求参数
@Data
public class LoginDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String captchaKey;   // 验证码 key
    private String captchaCode;  // 验证码 code
}

// 登录响应数据
@Data
public class LoginVO {
    private String accessToken;   // 访问令牌
    private String refreshToken;   // 刷新令牌
    private Long expiresIn;        // 过期时间（秒）
    private String tokenType;      // 令牌类型
    private UserInfo userInfo;    // 用户信息
}

@Data
public class UserInfo {
    private Long userId;
    private String username;
    private String nickName;
    private String avatar;
    private List<String> roles;    // 角色标识列表
    private List<String> permissions; // 权限标识列表
}
```

### 5.17 登录用户信息类

```java
@Data
@EqualsAndHashCode(callSuper = true)
public class LoginUser implements UserDetails, Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 角色标识列表
     */
    private List<String> roles;

    /**
     * 权限标识列表
     */
    private List<String> permissions;

    /**
     * 登录时间
     */
    private Date loginTime;

    /**
     * 登录IP地址
     */
    private String loginIp;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (permissions == null || permissions.isEmpty()) {
            return Collections.emptyList();
        }
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

### 5.18 JWT 工具类

```java
@Component
@RequiredArgsConstructor
public class JwtTokenUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expire-time}")
    private Long accessTokenExpireTime;

    @Value("${jwt.refresh-token-expire-time}")
    private Long refreshTokenExpireTime;

    private static final String ACCESS_TOKEN_PREFIX = "jwt:access:";
    private static final String REFRESH_TOKEN_PREFIX = "jwt:refresh:";
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(LoginUser loginUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", loginUser.getUserId());
        claims.put("username", loginUser.getUsername());
        claims.put("roles", loginUser.getRoles());
        claims.put("permissions", loginUser.getPermissions());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(loginUser.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpireTime))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        // 存入 Redis
        redisTemplate.opsForValue().set(
                ACCESS_TOKEN_PREFIX + loginUser.getUserId(),
                token,
                accessTokenExpireTime,
                TimeUnit.MILLISECONDS
        );

        return token;
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(LoginUser loginUser) {
        String token = Jwts.builder()
                .setSubject(loginUser.getUsername())
                .claim("userId", loginUser.getUserId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpireTime))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS256)
                .compact();

        // 存入 Redis
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + loginUser.getUserId(),
                token,
                refreshTokenExpireTime,
                TimeUnit.MILLISECONDS
        );

        return token;
    }

    /**
     * 解析 Token
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 验证 Token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * 检查 Token 是否在黑名单
     */
    public boolean isInBlacklist(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    /**
     * 将 Token 加入黑名单
     */
    public void addToBlacklist(String token, long expiration) {
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "1",
                expiration,
                TimeUnit.MILLISECONDS
        );
    }

    /**
     * 从 Token 获取用户信息
     */
    public LoginUser getLoginUser(String token) {
        Claims claims = parseToken(token);
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(Long.valueOf(claims.get("userId").toString()));
        loginUser.setUsername(claims.getSubject());

        Object roles = claims.get("roles");
        if (roles != null) {
            loginUser.setRoles((List<String>) roles);
        }

        Object permissions = claims.get("permissions");
        if (permissions != null) {
            loginUser.setPermissions((List<String>) permissions);
        }

        return loginUser;
    }

    /**
     * 移除用户 Token
     */
    public void removeToken(Long userId) {
        redisTemplate.delete(ACCESS_TOKEN_PREFIX + userId);
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }
}
```

### 5.19 JWT 认证过滤器

```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final SysMenuService menuService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token)) {
            // 验证 Token 格式
            if (!jwtTokenUtil.validateToken(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 检查黑名单
            if (jwtTokenUtil.isInBlacklist(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 解析用户信息
            LoginUser loginUser = jwtTokenUtil.getLoginUser(token);

            // 查询用户的最新权限
            List<String> permissions = menuService.getPermissionsByUserId(loginUser.getUserId());
            loginUser.setPermissions(permissions);

            // 设置认证信息
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            loginUser,
                            null,
                            loginUser.getAuthorities()
                    );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头获取 Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### 5.20 Redis 配置类

```java
@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);

        // 使用 Jackson 序列化
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(
                objectMapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        serializer.setObjectMapper(objectMapper);

        // 设置 Key 序列化
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 设置 Value 序列化
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}
```

### 5.21 Web 安全配置

**安全配置属性类**：
```java
@Data
@Component
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

    /**
     * 白名单路径列表（支持 Ant 路径模式）
     */
    private List<String> whiteList = new ArrayList<>();
}
```

**Web 安全配置**：
```java
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final SecurityProperties securityProperties;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF
            .csrf(AbstractHttpConfigurer::disable)

            // 配置请求授权
            .authorizeHttpRequests(auth -> auth
                // 白名单接口（从配置文件读取）
                .requestMatchers(securityProperties.getWhiteList().toArray(new String[0]))
                    .permitAll()
                // 其他接口需要认证
                .anyRequest().authenticated()
            )

            // 配置异常处理
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authenticationEntryPoint)
            )

            // 添加 JWT 过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            // 禁用 Session
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

**配置示例**（application.yml）：
```yaml
security:
  white-list:
    - /api/auth/**
    - /doc.html
    - /swagger-ui/**
    - /swagger-ui.html
    - /v3/api-docs/**
    - /v3/api-docs.yaml
    - /webjars/**
    - /actuator/health
```

### 5.22 跨域配置

前后端分离项目需要配置跨域支持：

**跨域配置属性类**：
```java
@Data
@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    /**
     * 允许的来源（Origins）
     */
    private List<String> allowedOrigins = new ArrayList<>();

    /**
     * 允许的请求方法
     */
    private List<String> allowedMethods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");

    /**
     * 允许的请求头
     */
    private List<String> allowedHeaders = Arrays.asList("*");

    /**
     * 是否允许携带凭证（Cookie）
     */
    private Boolean allowCredentials = true;

    /**
     * 预检请求缓存时间（秒）
     */
    private Long maxAge = 3600L;
}
```

**跨域配置类**：
```java
@Configuration
@RequiredArgsConstructor
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
                .allowedMethods(corsProperties.getAllowedMethods().toArray(new String[0]))
                .allowedHeaders(corsProperties.getAllowedHeaders().toArray(new String[0]))
                .allowCredentials(corsProperties.getAllowCredentials())
                .maxAge(corsProperties.getMaxAge());
    }
}
```

**配置示例**（application.yml）：
```yaml
# 跨域配置
cors:
  allowed-origins:
    - http://localhost:3000
    - http://localhost:8080
    - https://your-domain.com
  allowed-methods:
    - GET
    - POST
    - PUT
    - DELETE
    - OPTIONS
  allowed-headers:
    - "*"
  allow-credentials: true
  max-age: 3600
```

**说明**：
- `allowed-origins` 支持多个域名配置
- 生产环境应限制为实际的域名地址
- `allow-credentials: true` 时，`allowed-origins` 不能使用 `*`
- Spring Security 中需同步配置允许跨域：
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // ... 其他配置
        .cors(cors -> cors.configurationSource(corsConfigurationSource()));

    return http.build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

### 5.23 自定义认证入口点

```java
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        R<Void> result = R.fail(ResponseCode.UNAUTHORIZED.getCode(),
                "未登录或登录已过期，请重新登录");

        response.getWriter().write(JSON.toJSONString(result));
    }
}
```

### 5.24 权限校验切面

```java
@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final SysMenuService menuService;
    private final JwtTokenUtil jwtTokenUtil;

    @Around("@annotation(requiresPermissions)")
    public Object around(ProceedingJoinPoint joinPoint,
                       RequiresPermissions requiresPermissions) throws Throwable {
        // 获取当前登录用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof LoginUser)) {
            throw new BusinessException(ResponseCode.UNAUTHORIZED);
        }

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

        // 获取注解上的权限标识
        String[] perms = requiresPermissions.value();
        Logical logical = requiresPermissions.logical();

        // 校验权限
        boolean hasPermission = checkPermissions(loginUser.getPermissions(), perms, logical);
        if (!hasPermission) {
            throw new BusinessException(ResponseCode.FORBIDDEN);
        }

        return joinPoint.proceed();
    }

    private boolean checkPermissions(List<String> userPermissions,
                                   String[] requiredPermissions,
                                   Logical logical) {
        if (userPermissions == null || userPermissions.isEmpty()) {
            return false;
        }

        if (logical == Logical.AND) {
            // 需要满足所有权限
            for (String perm : requiredPermissions) {
                if (!userPermissions.contains(perm)) {
                    return false;
                }
            }
            return true;
        } else {
            // 满足任一权限即可
            for (String perm : requiredPermissions) {
                if (userPermissions.contains(perm)) {
                    return true;
                }
            }
            return false;
        }
    }
}
```

### 5.25 操作日志切面

```java
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogAspect {

    private final SysOperLogService operLogService;

    @Around("@annotation(log)")
    public Object around(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        String operParam = log.saveParam() ? JSON.toJSONString(args) : null;

        // 获取请求信息
        HttpServletRequest request = getHttpServletRequest();

        // 执行目标方法
        Object result = null;
        int operStatus = 0;
        String errorMsg = null;

        try {
            result = joinPoint.proceed();
            operStatus = 0;
        } catch (Exception e) {
            operStatus = 1;
            errorMsg = e.getMessage();
            throw e;
        } finally {
            // 保存操作日志
            long costTime = System.currentTimeMillis() - startTime;
            saveOperLog(joinPoint, log, request, operParam, operStatus, errorMsg, costTime);
        }

        return result;
    }

    private void saveOperLog(ProceedingJoinPoint joinPoint, Log log,
                            HttpServletRequest request, String operParam,
                            int operStatus, String errorMsg, long costTime) {
        try {
            SysOperLog operLog = new SysOperLog();
            operLog.setTitle(log.title());
            operLog.setBusinessType(log.businessType().getCode());
            operLog.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." +
                    joinPoint.getSignature().getName());
            operLog.setRequestMethod(request.getMethod());
            operLog.setOperParam(operParam);
            operLog.setOperStatus(operStatus);
            operLog.setErrorMsg(errorMsg);
            operLog.setCostTime(costTime);
            operLog.setOperTime(LocalDateTime.now());

            // 获取登录用户
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof LoginUser) {
                LoginUser loginUser = (LoginUser) authentication.getPrincipal();
                operLog.setUserId(loginUser.getUserId());
                operLog.setUserName(loginUser.getUsername());
            }

            // 获取 IP 地址
            operLog.setIpaddr(getIpAddress(request));
            operLog.setOperLocation(getCityByIp(operLog.getIpaddr()));
            operLog.setUrl(request.getRequestURI());

            operLogService.save(operLog);
        } catch (Exception e) {
            log.error("保存操作日志失败", e);
        }
    }

    private HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0.0.0.0".equals(ip) ? "127.0.0.1" : ip;
    }

    private String getCityByIp(String ip) {
        // 实际项目中可使用 IP 库或第三方服务
        return "内网IP";
    }
}
```

---

## 六、配置文件

### 6.1 application.yml

```yaml
server:
  port: 8080
  servlet:
    context-path: /

spring:
  application:
    name: admin-boot
  profiles:
    active: dev
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:admin_boot}?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&useSSL=false
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root123}
    hikari:
      minimum-idle: 5
      maximum-pool-size: 20
      idle-timeout: 300000
      connection-timeout: 20000
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      database: ${REDIS_DB:0}
      timeout: 3000ms
      lettuce:
        pool:
          max-active: 8
          max-idle: 8
          min-idle: 0
          max-wait: -1ms
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 20MB

mybatis-plus:
  mapper-locations: classpath*:/mapper/**/*.xml
  type-aliases-package: com.admin.**.entity
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: delFlag
      logic-delete-value: 1
      logic-not-delete-value: 0
    banner: false

# 安全配置
security:
  white-list:
    - /api/auth/**
    - /doc.html
    - /swagger-ui/**
    - /swagger-ui.html
    - /v3/api-docs/**
    - /v3/api-docs.yaml
    - /webjars/**
    - /actuator/health

# JWT配置
jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here-change-in-production}
  access-token-expire-time: 900000      # 15分钟（毫秒）
  refresh-token-expire-time: 604800000   # 7天（毫秒）

# 文件存储配置
file:
  upload-path: ${FILE_UPLOAD_PATH:/data/upload}
  storage-type: ${FILE_STORAGE_TYPE:local}
  allowed-extensions: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,zip,rar

# 接口限流配置
rate-limit:
  enabled: true
  login:
    max-requests: 5
    window-seconds: 60
  api:
    max-requests: 100
    window-seconds: 60

# Knife4j文档配置
knife4j:
  enable: true
  setting:
    language: zh_cn

# Flyway 数据库迁移配置
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true
    locations: classpath:db/migration
    sql-migration-prefix: V
    sql-migration-separator:__
    sql-migration-suffixes: .sql
    encoding: UTF-8
```

### 6.2 Flyway 数据库迁移

本项目使用 Flyway 管理数据库版本，支持 SQL 脚本自动迁移。

**迁移脚本命名规范**：
```
V{版本号}__{描述}.sql
示例：
- V1__init.sql           # 初始版本
- V1.1__add_user_phone.sql  # 小版本升级
- V2.0.0__add_order.sql     # 大版本升级
```

**迁移脚本目录结构**：
```
resources/
└── db/
    └── migration/
        ├── V1__init.sql           # 初始化脚本
        ├── V1.1__add_user_phone.sql  # 增量脚本
        └── V2.0__add_order.sql       # 大版本脚本
```

**V1__init.sql 示例**：
```sql
-- 初始化数据库表结构
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(200) NOT NULL COMMENT '密码',
    `nick_name` VARCHAR(50) COMMENT '昵称',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `sex` TINYINT DEFAULT 0 COMMENT '性别',
    `avatar` VARCHAR(255) COMMENT '头像',
    `status` TINYINT DEFAULT 1 COMMENT '状态',
    `del_flag` TINYINT DEFAULT 0 COMMENT '删除标志',
    `create_by` VARCHAR(64) COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```

**各环境 Flyway 配置**：

```yaml
# 开发环境 - 允许清理数据库
spring:
  flyway:
    enabled: true
    clean-disabled: false  # 允许 flyway:clean

# 生产环境 - 禁止清理数据库
spring:
  flyway:
    enabled: true
    clean-disabled: true   # 禁止 flyway:clean
```

**常用 Flyway 命令**：
```bash
# 启动时自动执行迁移
mvn spring-boot:run

# 手动执行迁移
mvn flyway:migrate

# 查看当前版本
mvn flyway:info

# 清理数据库（慎用，仅开发环境）
mvn flyway:clean

# 修复版本记录
mvn flyway:repair
```

**迁移注意事项**：
- 每次数据库变更必须创建新的迁移脚本
- 脚本只允许新增，禁止修改已执行的脚本
- 大版本迁移建议分开多个小版本执行
- 生产环境禁止使用 `flyway:clean`

### 6.3 application-dev.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/admin_boot_dev?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
  redis:
    database: 0

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl

# 开发环境允许 Flyway 清理
spring:
  flyway:
    clean-disabled: false
```

### 6.4 application-prod.yml

```yaml
spring:
  datasource:
    hikari:
      minimum-idle: 10
      maximum-pool-size: 50
  redis:
    database: 1

mybatis-plus:
  configuration:
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl

rate-limit:
  enabled: true
```

### 6.5 日志配置

本项目使用 Logback 作为日志框架，配置文件位于 `resources/logback-spring.xml`。

**logback-spring.xml 配置示例**：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 定义日志格式 -->
    <property name="LOG_PATTERN" value="%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"/>
    <property name="LOG_FILE" value="${LOG_FILE:-logs/admin-boot}"/>

    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
    </appender>

    <!-- 应用日志文件 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}.log</file>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <!-- 滚动策略：按日期 -->
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxHistory>30</maxHistory>
            <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>100MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>

    <!-- 错误日志单独记录 -->
    <appender name="ERROR_FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOG_FILE}-error.log</file>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level>
            <onMatch>ACCEPT</onMatch>
            <onMismatch>DENY</onMismatch>
        </filter>
        <encoder>
            <pattern>${LOG_PATTERN}</pattern>
            <charset>UTF-8</charset>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>${LOG_FILE}-error.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>90</maxHistory>
        </rollingPolicy>
    </appender>

    <!-- 开发环境配置 -->
    <springProfile name="dev">
        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
        <!-- MyBatis SQL 日志 -->
        <logger name="com.admin.mapper" level="DEBUG"/>
    </springProfile>

    <!-- 生产环境配置 -->
    <springProfile name="prod">
        <root level="WARN">
            <appender-ref ref="FILE"/>
            <appender-ref ref="ERROR_FILE"/>
        </root>
        <logger name="com.admin" level="INFO"/>
        <!-- 关闭 MyBatis SQL 日志 -->
        <logger name="com.admin.mapper" level="WARN"/>
    </springProfile>

</configuration>
```

**application.yml 中配置日志**：
```yaml
logging:
  level:
    root: INFO
    com.admin: DEBUG
    com.admin.mapper: DEBUG  # 开发环境打印 SQL
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/admin-boot.log
    max-size: 100MB
    max-history: 30
```

**日志级别说明**：

| 级别 | 优先级 | 使用场景 |
|------|--------|----------|
| DEBUG | 0 | 开发调试，SQL 语句、变量值 |
| INFO | 1 | 正常运行信息，接口调用、业务流程 |
| WARN | 2 | 警告信息，可恢复的异常 |
| ERROR | 3 | 错误信息，需要关注的异常 |

**日志输出规范**：
```java
// 推荐：使用 Slf4j 日志门面
@Slf4j
@Service
public class UserService {

    public void createUser(UserDTO dto) {
        // 使用占位符，避免字符串拼接
        log.info("创建用户: username={}", dto.getUsername());
        // ...
        log.info("用户创建成功: userId={}", user.getId());
    }

    public void updateUser(Long id, UserDTO dto) {
        try {
            // ...
        } catch (Exception e) {
            // 记录异常时使用 error，带参数
            log.error("更新用户失败: userId={}, error={}", id, e.getMessage(), e);
        }
    }
}
```

**各环境日志策略**：

| 环境 | 日志级别 | 输出位置 | SQL 日志 |
|------|----------|----------|----------|
| dev | DEBUG | 控制台 | 开启 |
| test | INFO | 文件 | 关闭 |
| prod | WARN | 文件 | 关闭 |

---

## 七、代码生成器规范

### 7.1 生成的文件

| 文件类型 | 路径 | 说明 |
|----------|------|------|
| Entity | `{module}/entity/{TableName}.java` | 数据实体，与表对应 |
| Mapper | `{module}/mapper/{TableName}Mapper.java` | 数据访问层 |
| Service接口 | `{module}/service/I{TableName}Service.java` | 服务接口 |
| Service实现 | `{module}/service/impl/{TableName}ServiceImpl.java` | 服务实现 |
| Controller | `{module}/controller/{TableName}Controller.java` | 控制器 |
| 新增DTO | `{module}/dto/Add{TableName}DTO.java` | 新增请求参数 |
| 修改DTO | `{module}/dto/Update{TableName}DTO.java` | 修改请求参数 |
| Query | `{module}/query/{TableName}Query.java` | 分页查询参数 |
| VO | `{module}/vo/{TableName}VO.java` | 响应视图对象 |
| Converter | `{module}/converter/{TableName}Converter.java` | 对象转换器 |
| Mapper XML | `resources/mapper/{module}/{tableName}.xml` | MyBatis XML |
| Menu SQL | `sql/menu_{tableName}.sql` | 菜单初始化SQL |

**注**：`{module}` 为业务模块名，如 `user`、`role`、`product`。

### 7.2 生成代码模板

生成的Controller包含：
- 分页列表接口（GET /list）
- 详情接口（GET /{id}）
- 新增接口（POST）
- 修改接口（PUT /{id}）
- 删除接口（DELETE /{id}）
- 批量删除接口（DELETE /batch/{ids}）

生成的Service包含：
- 分页查询
- 根据ID查询
- 新增
- 修改
- 删除
- 批量删除
- 下拉列表查询

生成的Mapper继承BaseMapper，使用MyBatis-Plus提供的CRUD。

### 7.3 生成菜单SQL模板

```sql
-- 菜单SQL模板
-- parent_id: 上级菜单ID，0表示顶级菜单
-- menu_type: M=目录, C=菜单, F=按钮
-- perms: 权限标识，如 system:user:list

-- 目录
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_time) VALUES
('模块管理', 0, 1, 'module', NULL, 'M', 0, 1, '', 'system', NOW());

-- 菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_time) VALUES
('XXX管理', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = '模块管理') t), 1, 'xxx', 'system/xxx/index', 'C', 0, 1, '', 'list', NOW());

-- 按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_time) VALUES
('XXX查询', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = 'XXX管理') t), 1, '', '', 'F', 0, 1, 'module:xxx:query', '#', NOW()),
('XXX新增', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = 'XXX管理') t), 2, '', '', 'F', 0, 1, 'module:xxx:add', '#', NOW()),
('XXX修改', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = 'XXX管理') t), 3, '', '', 'F', 0, 1, 'module:xxx:edit', '#', NOW()),
('XXX删除', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = 'XXX管理') t), 4, '', '', 'F', 0, 1, 'module:xxx:remove', '#', NOW()),
('XXX导出', (SELECT id FROM (SELECT id FROM sys_menu WHERE menu_name = 'XXX管理') t), 5, '', '', 'F', 0, 1, 'module:xxx:export', '#', NOW());
```

---

## 八、安全规范

### 8.1 JWT Token 校验流程

```
请求 → JwtAuthenticationFilter
        ↓
    校验Token格式
        ↓ (失败)
    返回 401 Unauthorized
        ↓ (成功)
    检查Token黑名单
        ↓ (在黑名单)
    返回 401 Token已加入黑名单
        ↓ (不在黑名单)
    解析用户信息 → 验证Token有效性
        ↓
    设置SecurityContext
        ↓
    放行请求
```

### 8.2 权限校验流程

```
请求 → @RequiresPermissions注解
        ↓
    获取用户角色列表（从Token）
        ↓
    查询角色关联的菜单权限
        ↓
    校验是否有目标权限
        ↓ (无权限)
    返回 403 Forbidden
        ↓ (有权限)
    执行目标方法
```

### 8.3 Refresh Token 黑名单机制

用户登出时，将 Refresh Token 加入 Redis 黑名单：

```java
// 登出时执行
redisTemplate.opsForValue().set(
    "jwt:blacklist:refresh:" + refreshToken,
    "1",
    Duration.ofMillis(refreshTokenExpireTime)
);
```

校验 Refresh Token 时，先检查是否在黑名单中。

### 8.4 接口限流

| 接口类型 | 限制规则 | 说明 |
|----------|----------|------|
| 登录接口 | 5次/分钟/IP | 防止暴力破解 |
| 注册接口 | 3次/分钟/IP | 防止恶意注册 |
| 普通API | 100次/分钟/用户 | 防止滥用 |

### 8.5 敏感操作日志

以下操作必须记录操作日志：
- 新增（INSERT）
- 修改（UPDATE）
- 删除（DELETE）
- 授权（GRANT）
- 导出（EXPORT）
- 导入（IMPORT）

### 8.6 密码安全

- 密码加密：BCryptPasswordEncoder
- 密码长度：8-20位
- 密码策略：大写字母 + 小写字母 + 数字 + 特殊字符
- 错误锁定：连续5次密码错误，锁定15分钟
- 历史密码：不许使用最近3次使用过的密码

---

## 九、开发规范

### 9.1 命名规范

| 类型 | 规范 | 示例 |
|------|------|------|
| 表名 | sys_模块名 | sys_user, sys_role |
| 实体类 | 表名转驼峰 | SysUser |
| Mapper | 表名+Mapper | SysUserMapper |
| Service | I表名+Service | ISysUserService |
| Service实现 | 表名+ServiceImpl | SysUserServiceImpl |
| Controller | 表名+Controller | SysUserController |
| DTO | Add/Update+表名+DTO | AddUserDTO |
| Query | 表名+Query | SysUserQuery |
| VO | 表名+VO | SysUserVO |
| Converter | 表名+Converter | SysUserConverter |

### 9.2 分层职责

| 层 | 职责 | 注意事项 |
|----|------|----------|
| Controller | 参数校验、响应封装、调用Service | 不写业务逻辑 |
| Service | 业务逻辑、事务管理 | 事务边界要清晰 |
| Mapper | 数据库操作 | 不写复杂SQL，复杂SQL放XML |
| Entity | 数据模型 | 与数据库表一一对应 |
| DTO | 接收前端请求参数 | 使用@Validated校验 |
| Query | 分页查询参数 | 继承PageQuery |
| VO | 返回给前端的数据 | 可脱敏、格式化 |

### 9.3 异常处理

- 使用全局异常处理器统一处理
- 业务异常使用 `BusinessException`
- 系统异常使用 `SystemException`
- 所有异常统一返回 `R.fail()`

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public R<Void> handleBusinessException(BusinessException e) {
        return R.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return R.fail(ResponseCode.BAD_REQUEST.getCode(), message);
    }

    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e) {
        log.error("System error", e);
        return R.fail(ResponseCode.SYSTEM_ERROR.getCode(), "系统异常");
    }
}
```

### 9.4 数据库规范

- 主键使用自增 BIGINT
- 状态字段使用 TINYINT
- 删除标志使用 del_flag（逻辑删除）
- 时间字段使用 DATETIME
- 字符串默认 VARCHAR(255)
- IP字段使用 VARCHAR(45)
- 密码字段使用 VARCHAR(200)
- 必须添加索引
- 避免 SELECT *

---

## 十、初始化数据

### 10.1 初始用户

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| admin | admin123 | 超级管理员 | 系统管理员，拥有所有权限 |

### 10.2 初始菜单

```
系统管理
├── 用户管理
│   ├── 查看用户列表 (system:user:list)
│   ├── 用户查询 (system:user:query)
│   ├── 用户新增 (system:user:add)
│   ├── 用户修改 (system:user:edit)
│   ├── 用户删除 (system:user:remove)
│   └── 用户导出 (system:user:export)
├── 角色管理
│   ├── 查看角色列表 (system:role:list)
│   ├── 角色查询 (system:role:query)
│   ├── 角色新增 (system:role:add)
│   ├── 角色修改 (system:role:edit)
│   ├── 角色删除 (system:role:remove)
│   ├── 角色导出 (system:role:export)
│   └── 分配菜单 (system:role:edit)
├── 菜单管理
│   ├── 查看菜单列表 (system:menu:list)
│   ├── 菜单查询 (system:menu:query)
│   ├── 菜单新增 (system:menu:add)
│   ├── 菜单修改 (system:menu:edit)
│   └── 菜单删除 (system:menu:remove)
├── 字典管理
│   ├── 字典类型
│   └── 字典数据
├── 参数设置
├── 操作日志
└── 登录日志
```

### 10.3 初始字典

| 字典类型 | 字典标签 | 字典值 | 说明 |
|----------|----------|--------|------|
| sys_user_sex | 未知 | 0 | 性别-未知 |
| sys_user_sex | 男 | 1 | 性别-男 |
| sys_user_sex | 女 | 2 | 性别-女 |
| sys_normal_disable | 正常 | 1 | 状态-正常 |
| sys_normal_disable | 禁用 | 0 | 状态-禁用 |
| sys_menu_type | 目录 | M | 菜单类型-目录 |
| sys_menu_type | 菜单 | C | 菜单类型-菜单 |
| sys_menu_type | 按钮 | F | 菜单类型-按钮 |
| sys_show_hide | 显示 | 0 | 菜单状态-显示 |
| sys_show_hide | 隐藏 | 1 | 菜单状态-隐藏 |

---

## 十一、扩展指南

### 11.1 添加新业务模块

1. 设计数据库表
2. 执行代码生成器生成基础CRUD
3. 补充业务逻辑
4. 在菜单表中添加对应菜单
5. 给角色分配菜单权限

### 11.2 升级文件存储

1. 实现 FileStorageService 接口
2. 使用 @ConditionalOnProperty 指定激活条件
3. 修改配置文件切换存储方式

```java
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "file.storage-type", havingValue = "oss")
public class OssFileStorageService implements FileStorageService {
    // OSS 实现
}
```

### 11.3 如需添加数据权限

当前系统未设计数据权限功能。如后续需要：
1. 在 sys_user 表添加 dept_id 字段
2. 在 sys_role 表添加 data_scope 字段
3. 在 BaseService 中添加数据范围过滤逻辑
4. 使用 MyBatis-Plus 插件实现

### 11.4 如需升级为微服务

1. 将 admin-system 抽取为独立服务
2. 使用 Nacos 作为注册中心和配置中心
3. 使用 Gateway 作为网关
4. 使用 Feign 进行服务间调用
5. 添加消息队列进行异步解耦

---

## 附录

### A. 依赖版本管理

```xml
<!-- 父 POM 中统一管理版本 -->
<properties>
    <java.version>17</java.version>
    <spring-boot.version>3.2.5</spring-boot.version>
    <mybatis-plus.version>3.5.7</mybatis-plus.version>
    <flyway.version>10.10.0</flyway.version>
    <jjwt.version>0.12.5</jjwt.version>
    <knife4j.version>4.5.0</knife4j.version>
    <hutool.version>5.8.28</hutool.version>
    <lombok.version>1.18.32</lombok.version>
</properties>
```

```xml
<!-- 依赖管理中添加 -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
    <version>${flyway.version}</version>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
    <version>${flyway.version}</version>
</dependency>
```

**admin-system 模块中引入**：
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-mysql</artifactId>
</dependency>
```

### B. 环境变量说明

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| DB_HOST | 数据库地址 | localhost |
| DB_PORT | 数据库端口 | 3306 |
| DB_NAME | 数据库名称 | admin_boot |
| DB_USERNAME | 数据库用户名 | root |
| DB_PASSWORD | 数据库密码 | root123 |
| REDIS_HOST | Redis地址 | localhost |
| REDIS_PORT | Redis端口 | 6379 |
| REDIS_PASSWORD | Redis密码 | (空) |
| REDIS_DB | Redis数据库 | 0 |
| JWT_SECRET | JWT密钥 | (需配置) |
| FILE_UPLOAD_PATH | 文件上传路径 | /data/upload |
| FILE_STORAGE_TYPE | 存储类型 | local |
| SECURITY_WHITE_LIST | 安全白名单 | (见security.white-list配置) |

---

*文档版本：v2.8*
*最后更新：2026年5月22日*
