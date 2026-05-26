package com.admin.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 操作日志记录实体
 */
@Schema(description = "操作日志记录实体")
@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "日志主键", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "模块标题")
    private String title;

    @Schema(description = "业务类型（0=其他，1=新增，2=修改，3=删除...）")
    private Integer businessType;

    @Schema(description = "请求方法")
    private String method;

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "操作类别（0=后台用户，1=手机端用户）")
    private Integer operatorType;

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
