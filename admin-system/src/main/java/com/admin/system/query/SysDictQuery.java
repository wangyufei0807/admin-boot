package com.admin.system.query;

import com.admin.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典类型查询参数
 */
@Schema(description = "字典类型查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictQuery extends PageQuery {

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    private Integer status;
}
