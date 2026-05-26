package com.admin.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 修改头像请求参数
 */
@Schema(description = "修改头像请求参数")
@Data
public class UpdateAvatarDTO {

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long id;

    @Schema(description = "头像URL")
    private String avatar;
}
