package com.admin.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 跨域配置属性类
 */
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
    private List<String> allowedMethods = new ArrayList<>();

    /**
     * 允许的请求头
     */
    private List<String> allowedHeaders = new ArrayList<>();

    /**
     * 是否允许携带凭证（Cookie）
     */
    private Boolean allowCredentials = true;

    /**
     * 预检请求缓存时间（秒）
     */
    private Long maxAge = 3600L;
}
