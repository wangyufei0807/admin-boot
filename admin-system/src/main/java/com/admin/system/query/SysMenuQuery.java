package com.admin.system.query;

import com.admin.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单查询参数
 */
@Schema(description = "菜单查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysMenuQuery extends PageQuery {

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    private Integer status;
}
