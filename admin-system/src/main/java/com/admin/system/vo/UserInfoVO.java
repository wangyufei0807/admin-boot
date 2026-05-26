package com.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 用户信息视图对象
 */
@Schema(description = "用户信息视图对象")
@Data
public class UserInfoVO {

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "角色标识列表")
    private List<String> roles;

    @Schema(description = "权限标识列表")
    private List<String> permissions;
}
