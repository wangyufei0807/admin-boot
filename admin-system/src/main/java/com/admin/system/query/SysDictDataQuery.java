package com.admin.system.query;

import com.admin.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 字典数据查询参数
 */
@Schema(description = "字典数据查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictDataQuery extends PageQuery {

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "字典标签")
    private String dictLabel;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    private Integer status;
}
