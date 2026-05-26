package com.admin.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 新增配置请求参数
 */
@Schema(description = "新增配置请求参数")
@Data
public class AddConfigDTO {

    @Schema(description = "配置名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "配置名称不能为空")
    private String configName;

    @Schema(description = "配置key", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "配置key不能为空")
    private String configKey;

    @Schema(description = "配置值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "配置值不能为空")
    private String configValue;

    @Schema(description = "系统内置（Y=是，N=否）")
    private String configType;

    @Schema(description = "备注")
    private String remark;
}
