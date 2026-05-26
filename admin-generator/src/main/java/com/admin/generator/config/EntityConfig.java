package com.admin.generator.config;

import lombok.Data;

/**
 * Entity 配置
 */
@Data
public class EntityConfig {
    private Boolean lombok;
    private Boolean swagger;
    private Boolean serialVersionUID;
    private Boolean camelCase;
}
