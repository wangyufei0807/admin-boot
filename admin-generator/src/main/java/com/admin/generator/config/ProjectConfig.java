package com.admin.generator.config;

import lombok.Data;

/**
 * 项目配置
 */
@Data
public class ProjectConfig {
    private String author;
    private String packageName;
    private Boolean autoRemovePre;
    private String tablePrefix;
    private Boolean fileOverride;
}
