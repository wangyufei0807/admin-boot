package com.admin.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * 跨域配置类
 * 
 * 改进说明：
 * - 移除允许所有请求头的通配符策略
 * - 明确列出允许的请求头
 * - 根据环境切换不同的 CORS 策略
 */
@Slf4j
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    public CorsConfig(CorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(corsProperties.getAllowedOrigins().toArray(new String[0]))
                .allowedMethods(corsProperties.getAllowedMethods().toArray(new String[0]))
                .allowedHeaders(corsProperties.getAllowedHeaders().toArray(new String[0]))
                .exposedHeaders("Authorization", "X-Total-Count", "X-Total-Page")
                .allowCredentials(corsProperties.getAllowCredentials())
                .maxAge(corsProperties.getMaxAge());

        log.info("CORS configuration initialized. Allowed origins: {}", corsProperties.getAllowedOrigins());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        // 允许的源
        config.setAllowedOrigins(corsProperties.getAllowedOrigins());
        
        // 允许的 HTTP 方法
        config.setAllowedMethods(corsProperties.getAllowedMethods());
        
        // 允许的请求头（移除通配符，明确列出）
        // ✅ 改进：不再使用 * 通配符
        config.setAllowedHeaders(corsProperties.getAllowedHeaders());
        
        // 暴露给前端的响应头
        config.setExposedHeaders(Arrays.asList(
                "Authorization",
                "X-Total-Count",
                "X-Total-Page",
                "X-Trace-Id",
                "Content-Disposition"
        ));
        
        // 是否支持 Cookie
        config.setAllowCredentials(corsProperties.getAllowCredentials());
        
        // 预检请求的缓存时间（秒）
        config.setMaxAge(corsProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        
        return source;
    }
}
