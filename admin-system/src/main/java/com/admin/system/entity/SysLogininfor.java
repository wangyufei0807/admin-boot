package com.admin.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统访问记录实体
 */
@Schema(description = "系统访问记录实体")
@Data
@TableName("sys_logininfor")
public class SysLogininfor implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "访问ID", example = "1")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "用户名称")
    private String userName;

    @Schema(description = "登录IP地址")
    private String ipaddr;

    @Schema(description = "登录地点")
    private String loginLocation;

    @Schema(description = "浏览器类型")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "登录状态（0=成功，1=失败）")
    private Integer loginStatus;

    @Schema(description = "提示消息")
    private String msg;

    @Schema(description = "登录时间")
    private LocalDateTime loginTime;
}
