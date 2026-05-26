package com.admin.system.query;

import com.admin.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志查询参数
 */
@Schema(description = "登录日志查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysLogininforQuery extends PageQuery {

    @Schema(description = "用户名", example = "admin")
    private String userName;

    @Schema(description = "登录状态（0=成功，1=失败）")
    private Integer loginStatus;

    @Schema(description = "开始日期", example = "2024-01-01")
    private String startDate;

    @Schema(description = "结束日期", example = "2024-12-31")
    private String endDate;
}
