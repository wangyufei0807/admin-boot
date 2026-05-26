package com.admin.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增用户请求参数
 */
@Schema(description = "新增用户请求参数")
@Data
public class AddUserDTO {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度为2-20个字符")
    private String username;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "邮箱")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "性别（0=未知，1=男，2=女）", example = "1")
    private Integer sex;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    private Integer status;

    @Schema(description = "角色ID列表")
    private Long[] roleIds;

    @Schema(description = "备注")
    private String remark;
}
