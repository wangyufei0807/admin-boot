package com.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应视图对象
 */
@Schema(description = "登录响应视图对象")
@Data
public class LoginVO {

    @Schema(description = "访问令牌")
    private String accessToken;

    @Schema(description = "刷新令牌")
    private String refreshToken;

    @Schema(description = "过期时间（秒）", example = "900")
    private Long expiresIn;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType;

    @Schema(description = "用户信息")
    private UserInfoVO userInfo;
}
