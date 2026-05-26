package com.admin.generator.config;

import lombok.Data;

/**
 * Controller 配置
 */
@Data
public class ControllerConfig {
    private Boolean crud;
    private Boolean pagination;
    private Boolean export;
    private Boolean import_;
    private Boolean restStyle;
    private String apiPrefix;
    private Boolean parameterObject;  // 新增：是否添加 @ParameterObject 注解
}
