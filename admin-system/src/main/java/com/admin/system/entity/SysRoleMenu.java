package com.admin.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色和菜单关联实体
 */
@Schema(description = "角色和菜单关联实体")
@Data
@TableName("sys_role_menu")
public class SysRoleMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    @Schema(description = "菜单ID", example = "1")
    private Long menuId;
}
