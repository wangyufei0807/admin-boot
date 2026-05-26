package com.admin.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改菜单请求参数
 */
@Schema(description = "修改菜单请求参数")
@Data
public class UpdateMenuDTO {

    @Schema(description = "菜单ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单名称不能为空")
    private String menuName;

    @Schema(description = "父菜单ID", example = "0")
    private Long parentId;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "路由参数")
    private String queryParam;

    @Schema(description = "是否为外链（0=是，1=否）")
    private Integer isFrame;

    @Schema(description = "是否缓存（0=缓存，1=不缓存）")
    private Integer isCache;

    @Schema(description = "菜单类型（M=目录，C=菜单，F=按钮）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    @Schema(description = "菜单状态（0=显示，1=隐藏）")
    private Integer visible;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    private Integer status;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "备注")
    private String remark;
}
