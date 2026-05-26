package com.admin.generator.config;

import lombok.Data;

/**
 * 输出配置
 */
@Data
public class OutputConfig {
    private String javaPath;
    private String resourcesPath;
    private String mapperXmlPath;
    private Boolean currentProject;
}
