package com.admin.system.entity;

import com.admin.common.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单权限实体
 */
@Schema(description = "菜单权限实体")
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "显示顺序")
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

    @Schema(description = "菜单类型（M=目录，C=菜单，F=按钮）")
    private String menuType;

    @Schema(description = "菜单状态（0=显示，1=隐藏）")
    private Integer visible;

    @Schema(description = "菜单状态（0=禁用，1=正常）")
    private Integer status;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单图标")
    private String icon;
}
