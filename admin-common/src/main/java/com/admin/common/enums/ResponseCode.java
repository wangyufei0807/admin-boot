package com.admin.common.enums;

/**
 * 响应码枚举
 */
public enum ResponseCode {
    SUCCESS(200, "操作成功"),

    // 4xx 请求/业务错误
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无访问权限"),

    // 5xx 系统错误
    SYSTEM_ERROR(500, "系统异常"),
    DB_ERROR(500, "数据库异常"),
    REDIS_ERROR(500, "缓存服务异常"),
    FILE_ERROR(500, "文件操作异常");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
