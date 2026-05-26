package com.admin.system.query;

import com.admin.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户查询参数
 */
@Schema(description = "用户查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserQuery extends PageQuery {

    @Schema(description = "用户名", example = "admin")
    private String username;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "状态（0=禁用，1=正常）", example = "1")
    private Integer status;

    @Schema(description = "开始日期", example = "2024-01-01")
    private String startDate;

    @Schema(description = "结束日期", example = "2024-12-31")
    private String endDate;
}
