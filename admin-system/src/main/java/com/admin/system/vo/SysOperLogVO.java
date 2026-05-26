package com.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志视图对象
 */
@Schema(description = "操作日志视图对象")
@Data
public class SysOperLogVO {

    @Schema(description = "日志主键", example = "1")
    private Long id;

    @Schema(description = "模块标题")
    private String title;

    @Schema(description = "业务类型")
    private Integer businessType;

    @Schema(description = "请求方法")
    private String method;

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "请求URL")
    private String url;

    @Schema(description = "IP地址")
    private String ipaddr;

    @Schema(description = "操作地点")
    private String operLocation;

    @Schema(description = "请求参数")
    private String operParam;

    @Schema(description = "返回参数")
    private String jsonResult;

    @Schema(description = "操作状态（0=正常，1=异常）")
    private Integer operStatus;

    @Schema(description = "错误消息")
    private String errorMsg;

    @Schema(description = "操作时间")
    private LocalDateTime operTime;

    @Schema(description = "消耗时间（毫秒）")
    private Long costTime;
}
