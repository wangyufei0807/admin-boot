package com.admin.system.entity;

import com.admin.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色实体
 */
@Schema(description = "角色实体")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "角色标识")
    private String roleKey;

    @Schema(description = "显示顺序")
    private Integer roleSort;

    @Schema(description = "菜单树选择组件是否严格级联")
    private Integer menuCheckStrictly;

    @Schema(description = "状态（0=禁用，1=正常）")
    private Integer status;

    @Schema(description = "删除标志（0=未删，1=已删）")
    private Integer delFlag;
}
