package com.admin.system.query;

import com.admin.common.base.PageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作日志查询参数
 */
@Schema(description = "操作日志查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
public class SysOperLogQuery extends PageQuery {

    @Schema(description = "模块标题")
    private String title;

    @Schema(description = "业务类型")
    private Integer businessType;

    @Schema(description = "操作人员")
    private String userName;

    @Schema(description = "操作状态（0=正常，1=异常）")
    private Integer operStatus;

    @Schema(description = "开始日期", example = "2024-01-01")
    private String startDate;

    @Schema(description = "结束日期", example = "2024-12-31")
    private String endDate;
}
