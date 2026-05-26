package com.admin.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增角色请求参数
 */
@Schema(description = "新增角色请求参数")
@Data
public class AddRoleDTO {

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    @Schema(description = "角色标识", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "角色标识不能为空")
    private String roleKey;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示顺序不能为空")
    private Integer roleSort;

    @Schema(description = "菜单树选择组件是否严格级联")
    private Integer menuCheckStrictly;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "菜单ID列表")
    private Long[] menuIds;
}
