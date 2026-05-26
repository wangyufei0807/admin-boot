package com.admin.system.entity;

import com.admin.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数配置实体
 */
@Schema(description = "参数配置实体")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_config")
public class SysConfig extends BaseEntity {

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置key")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "系统内置（Y=是，N=否）")
    private String configType;
}
