package com.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 参数配置视图对象
 */
@Schema(description = "参数配置视图对象")
@Data
public class SysConfigVO {

    @Schema(description = "配置ID", example = "1")
    private Long id;

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置key")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "系统内置（Y=是，N=否）")
    private String configType;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "备注")
    private String remark;
}
