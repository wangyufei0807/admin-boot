-- ============================================
-- Admin-Boot 初始化脚本
-- 版本: V1__init.sql
-- 说明: 初始化系统管理相关表和基础数据
-- ============================================

-- ============================================
-- 系统管理相关表
-- ============================================

-- 用户表
CREATE TABLE IF NOT EXISTS sys_user (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 用户表索引
CREATE INDEX idx_user_status_del ON sys_user(status, del_flag);
CREATE INDEX idx_user_phone ON sys_user(phone);
CREATE INDEX idx_user_create_time ON sys_user(create_time);

-- 角色表
CREATE TABLE IF NOT EXISTS sys_role (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 角色表索引
CREATE INDEX idx_role_status_del ON sys_role(status, del_flag);

-- 菜单权限表
CREATE TABLE IF NOT EXISTS sys_menu (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='菜单权限表';

-- 菜单表索引
CREATE INDEX idx_menu_parent ON sys_menu(parent_id);
CREATE INDEX idx_menu_type_status ON sys_menu(menu_type, status);

-- 用户和角色关联表
CREATE TABLE IF NOT EXISTS sys_user_role (
    user_id         BIGINT          NOT NULL   COMMENT '用户ID',
    role_id         BIGINT          NOT NULL   COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户和角色关联表';

-- 角色和菜单关联表
CREATE TABLE IF NOT EXISTS sys_role_menu (
    role_id         BIGINT          NOT NULL   COMMENT '角色ID',
    menu_id         BIGINT          NOT NULL   COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和菜单关联表';

-- ============================================
-- 字典相关表
-- ============================================

-- 字典类型表
CREATE TABLE IF NOT EXISTS sys_dict (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典类型表';

-- 字典类型表索引
CREATE INDEX idx_dict_type ON sys_dict(dict_type);

-- 字典数据表
CREATE TABLE IF NOT EXISTS sys_dict_data (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='字典数据表';

-- 字典数据表索引
CREATE INDEX idx_dict_data_type ON sys_dict_data(dict_type);
CREATE INDEX idx_dict_data_status ON sys_dict_data(status);

-- ============================================
-- 日志相关表
-- ============================================

-- 系统访问记录表
CREATE TABLE IF NOT EXISTS sys_logininfor (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统访问记录表';

-- 登录日志表索引
CREATE INDEX idx_login_user ON sys_logininfor(user_id);
CREATE INDEX idx_login_status ON sys_logininfor(login_status);
CREATE INDEX idx_login_time ON sys_logininfor(login_time);

-- 操作日志记录表
CREATE TABLE IF NOT EXISTS sys_oper_log (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志记录表';

-- 操作日志表索引
CREATE INDEX idx_oper_user ON sys_oper_log(user_id);
CREATE INDEX idx_oper_status ON sys_oper_log(oper_status);
CREATE INDEX idx_oper_time ON sys_oper_log(oper_time);

-- ============================================
-- 配置相关表
-- ============================================

-- 参数配置表
CREATE TABLE IF NOT EXISTS sys_config (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='参数配置表';

-- 参数配置表索引
CREATE UNIQUE INDEX idx_config_key ON sys_config(config_key);

-- ============================================
-- 文件表
-- ============================================

CREATE TABLE IF NOT EXISTS sys_file (
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件表';

-- 文件表索引
CREATE INDEX idx_file_create_time ON sys_file(create_time);
CREATE INDEX idx_file_storage_type ON sys_file(storage_type);

-- ============================================
-- 初始化数据
-- ============================================

-- 插入超级管理员角色
INSERT INTO sys_role (role_name, role_key, role_sort, menu_check_strictly, status, del_flag, create_by, create_time, remark)
VALUES ('超级管理员', 'admin', 1, 1, 1, 0, 'system', NOW(), '系统内置角色，拥有所有权限');

-- 插入系统管理目录
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('系统管理', 0, 1, 'system', NULL, 'M', 0, 1, '', 'system', 'system', NOW(), '系统管理目录');

-- 使用会话变量避免 MySQL 1093：不能在同一条语句里对 sys_menu 既 INSERT 又从该表 SELECT
SET @system_menu_id = LAST_INSERT_ID();

-- 插入子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES
('用户管理', @system_menu_id, 1, 'user', 'system/user/index', 'C', 0, 1, 'system:user:list', 'user', 'system', NOW(), '用户管理菜单'),
('角色管理', @system_menu_id, 2, 'role', 'system/role/index', 'C', 0, 1, 'system:role:list', 'peoples', 'system', NOW(), '角色管理菜单'),
('菜单管理', @system_menu_id, 3, 'menu', 'system/menu/index', 'C', 0, 1, 'system:menu:list', 'tree-table', 'system', NOW(), '菜单管理菜单'),
('字典管理', @system_menu_id, 4, 'dict', 'system/dict/index', 'C', 0, 1, 'system:dict:list', 'international', 'system', NOW(), '字典管理菜单'),
('参数设置', @system_menu_id, 5, 'config', 'system/config/index', 'C', 0, 1, 'system:config:list', 'edit', 'system', NOW(), '参数设置菜单'),
('操作日志', @system_menu_id, 6, 'operlog', 'monitor/operlog/index', 'C', 0, 1, 'monitor:operlog:list', 'log', 'system', NOW(), '操作日志菜单'),
('登录日志', @system_menu_id, 7, 'logininfor', 'monitor/logininfor/index', 'C', 0, 1, 'monitor:logininfor:list', 'login-infor', 'system', NOW(), '登录日志菜单');

SET @user_menu_id = (SELECT id FROM sys_menu WHERE menu_name = '用户管理' LIMIT 1);
SET @role_menu_id = (SELECT id FROM sys_menu WHERE menu_name = '角色管理' LIMIT 1);
SET @menu_menu_id = (SELECT id FROM sys_menu WHERE menu_name = '菜单管理' LIMIT 1);
SET @dict_menu_id = (SELECT id FROM sys_menu WHERE menu_name = '字典管理' LIMIT 1);
SET @config_menu_id = (SELECT id FROM sys_menu WHERE menu_name = '参数设置' LIMIT 1);

-- 插入按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '用户查询', @user_menu_id, 1, '', '', 'F', 0, 1, 'system:user:query', '#', 'system', NOW()
UNION ALL SELECT '用户新增', @user_menu_id, 2, '', '', 'F', 0, 1, 'system:user:add', '#', 'system', NOW()
UNION ALL SELECT '用户修改', @user_menu_id, 3, '', '', 'F', 0, 1, 'system:user:edit', '#', 'system', NOW()
UNION ALL SELECT '用户删除', @user_menu_id, 4, '', '', 'F', 0, 1, 'system:user:remove', '#', 'system', NOW()
UNION ALL SELECT '用户导出', @user_menu_id, 5, '', '', 'F', 0, 1, 'system:user:export', '#', 'system', NOW()
UNION ALL SELECT '角色查询', @role_menu_id, 1, '', '', 'F', 0, 1, 'system:role:query', '#', 'system', NOW()
UNION ALL SELECT '角色新增', @role_menu_id, 2, '', '', 'F', 0, 1, 'system:role:add', '#', 'system', NOW()
UNION ALL SELECT '角色修改', @role_menu_id, 3, '', '', 'F', 0, 1, 'system:role:edit', '#', 'system', NOW()
UNION ALL SELECT '角色删除', @role_menu_id, 4, '', '', 'F', 0, 1, 'system:role:remove', '#', 'system', NOW()
UNION ALL SELECT '角色导出', @role_menu_id, 5, '', '', 'F', 0, 1, 'system:role:export', '#', 'system', NOW()
UNION ALL SELECT '菜单查询', @menu_menu_id, 1, '', '', 'F', 0, 1, 'system:menu:query', '#', 'system', NOW()
UNION ALL SELECT '菜单新增', @menu_menu_id, 2, '', '', 'F', 0, 1, 'system:menu:add', '#', 'system', NOW()
UNION ALL SELECT '菜单修改', @menu_menu_id, 3, '', '', 'F', 0, 1, 'system:menu:edit', '#', 'system', NOW()
UNION ALL SELECT '菜单删除', @menu_menu_id, 4, '', '', 'F', 0, 1, 'system:menu:remove', '#', 'system', NOW()
UNION ALL SELECT '字典查询', @dict_menu_id, 1, '', '', 'F', 0, 1, 'system:dict:query', '#', 'system', NOW()
UNION ALL SELECT '字典新增', @dict_menu_id, 2, '', '', 'F', 0, 1, 'system:dict:add', '#', 'system', NOW()
UNION ALL SELECT '字典修改', @dict_menu_id, 3, '', '', 'F', 0, 1, 'system:dict:edit', '#', 'system', NOW()
UNION ALL SELECT '字典删除', @dict_menu_id, 4, '', '', 'F', 0, 1, 'system:dict:remove', '#', 'system', NOW()
UNION ALL SELECT '参数查询', @config_menu_id, 1, '', '', 'F', 0, 1, 'system:config:query', '#', 'system', NOW()
UNION ALL SELECT '参数新增', @config_menu_id, 2, '', '', 'F', 0, 1, 'system:config:add', '#', 'system', NOW()
UNION ALL SELECT '参数修改', @config_menu_id, 3, '', '', 'F', 0, 1, 'system:config:edit', '#', 'system', NOW()
UNION ALL SELECT '参数删除', @config_menu_id, 4, '', '', 'F', 0, 1, 'system:config:remove', '#', 'system', NOW();

-- 给超级管理员分配所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT r.id, m.id
FROM sys_role r, sys_menu m
WHERE r.role_key = 'admin';

-- 插入超级管理员用户 (密码: admin123，使用BCrypt加密)
INSERT INTO sys_user (username, password, nick_name, email, phone, avatar, sex, status, del_flag, create_by, create_time, remark)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '管理员', 'admin@example.com', '13800138000', NULL, 1, 1, 0, 'system', NOW(), '系统内置管理员账户');

-- 给管理员分配超级管理员角色
INSERT INTO sys_user_role (user_id, role_id)
SELECT u.id, r.id
FROM sys_user u, sys_role r
WHERE u.username = 'admin' AND r.role_key = 'admin';

-- ============================================
-- 初始化字典数据
-- ============================================

-- 用户性别字典
INSERT INTO sys_dict (dict_name, dict_type, status, create_by, create_time, remark)
VALUES ('用户性别', 'sys_user_sex', 1, 'system', NOW(), '用户性别字典');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time)
VALUES
(1, '未知', '0', 'sys_user_sex', 'default', 1, 1, 'system', NOW()),
(2, '男', '1', 'sys_user_sex', 'primary', 0, 1, 'system', NOW()),
(3, '女', '2', 'sys_user_sex', 'danger', 0, 1, 'system', NOW());

-- 菜单状态字典
INSERT INTO sys_dict (dict_name, dict_type, status, create_by, create_time, remark)
VALUES ('菜单状态', 'sys_show_hide', 1, 'system', NOW(), '菜单状态字典');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time)
VALUES
(1, '显示', '0', 'sys_show_hide', 'primary', 1, 1, 'system', NOW()),
(2, '隐藏', '1', 'sys_show_hide', 'danger', 0, 1, 'system', NOW());

-- 菜单类型字典
INSERT INTO sys_dict (dict_name, dict_type, status, create_by, create_time, remark)
VALUES ('菜单类型', 'sys_menu_type', 1, 'system', NOW(), '菜单类型字典');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time)
VALUES
(1, '目录', 'M', 'sys_menu_type', 'warning', 0, 1, 'system', NOW()),
(2, '菜单', 'C', 'sys_menu_type', 'success', 0, 1, 'system', NOW()),
(3, '按钮', 'F', 'sys_menu_type', 'info', 0, 1, 'system', NOW());

-- 正常禁用字典
INSERT INTO sys_dict (dict_name, dict_type, status, create_by, create_time, remark)
VALUES ('正常禁用', 'sys_normal_disable', 1, 'system', NOW(), '系统正常禁用状态字典');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time)
VALUES
(1, '正常', '1', 'sys_normal_disable', 'primary', 1, 1, 'system', NOW()),
(2, '禁用', '0', 'sys_normal_disable', 'danger', 0, 1, 'system', NOW());

-- 登录状态字典
INSERT INTO sys_dict (dict_name, dict_type, status, create_by, create_time, remark)
VALUES ('登录状态', 'sys_login_status', 1, 'system', NOW(), '登录状态字典');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time)
VALUES
(1, '成功', '0', 'sys_login_status', 'success', 1, 1, 'system', NOW()),
(2, '失败', '1', 'sys_login_status', 'danger', 0, 1, 'system', NOW());

-- 操作日志状态字典
INSERT INTO sys_dict (dict_name, dict_type, status, create_by, create_time, remark)
VALUES ('操作日志状态', 'sys_oper_status', 1, 'system', NOW(), '操作日志状态字典');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time)
VALUES
(1, '正常', '0', 'sys_oper_status', 'primary', 1, 1, 'system', NOW()),
(2, '异常', '1', 'sys_oper_status', 'danger', 0, 1, 'system', NOW());

-- 业务类型字典
INSERT INTO sys_dict (dict_name, dict_type, status, create_by, create_time, remark)
VALUES ('业务类型', 'sys_business_type', 1, 'system', NOW(), '业务操作类型字典');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time)
VALUES
(1, '其他', '0', 'sys_business_type', 'info', 0, 1, 'system', NOW()),
(2, '新增', '1', 'sys_business_type', 'primary', 0, 1, 'system', NOW()),
(3, '修改', '2', 'sys_business_type', 'success', 0, 1, 'system', NOW()),
(4, '删除', '3', 'sys_business_type', 'danger', 0, 1, 'system', NOW()),
(5, '授权', '4', 'sys_business_type', 'warning', 0, 1, 'system', NOW()),
(6, '导出', '5', 'sys_business_type', 'primary', 0, 1, 'system', NOW()),
(7, '导入', '6', 'sys_business_type', 'success', 0, 1, 'system', NOW()),
(8, '查询', '7', 'sys_business_type', 'info', 0, 1, 'system', NOW());

-- 文件存储类型字典
INSERT INTO sys_dict (dict_name, dict_type, status, create_by, create_time, remark)
VALUES ('文件存储类型', 'sys_file_storage_type', 1, 'system', NOW(), '文件存储类型字典');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, list_class, is_default, status, create_by, create_time)
VALUES
(1, '本地存储', 'local', 'sys_file_storage_type', 'default', 1, 1, 'system', NOW()),
(2, 'OSS存储', 'oss', 'sys_file_storage_type', 'primary', 0, 1, 'system', NOW()),
(3, 'S3存储', 's3', 'sys_file_storage_type', 'success', 0, 1, 'system', NOW());

-- ============================================
-- 初始化系统配置
-- ============================================

INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, remark)
VALUES
('用户初始密码', 'sys.user.initPassword', 'admin123', 'Y', 'system', NOW(), '用户初始密码'),
('用户密码最小长度', 'sys.user.passwordMinLength', '6', 'Y', 'system', NOW(), '用户密码最小长度'),
('用户密码最大长度', 'sys.user.passwordMaxLength', '20', 'Y', 'system', NOW(), '用户密码最大长度'),
('是否开启用户注册', 'sys.user.registerEnabled', 'false', 'Y', 'system', NOW(), '是否开启用户注册功能'),
('文件上传最大大小', 'sys.file.maxSize', '10485760', 'Y', 'system', NOW(), '文件上传最大大小(字节)'),
('文件上传允许类型', 'sys.file.allowedExtensions', 'jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx', 'Y', 'system', NOW(), '文件上传允许的类型');
