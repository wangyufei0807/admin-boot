package com.admin.system.query;

import com.admin.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 参数配置查询参数
 */
@Schema(description = "参数配置查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysConfigQuery extends PageQuery {

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置key")
    private String configKey;

    @Schema(description = "配置类型")
    private String configType;
}
