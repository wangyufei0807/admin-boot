package com.admin.generator.config;

import lombok.Data;

/**
 * JDBC 配置
 */
@Data
public class JdbcConfig {
    private String driver;
    private String url;
    private String username;
    private String password;
}
