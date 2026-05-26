package com.admin.common.result;

import com.admin.common.enums.ResponseCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应封装
 *
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int code;
    private String message;
    private T data;

    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> R<T> ok() {
        return new R<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(ResponseCode.SUCCESS.getCode(), message, data);
    }

    public static <T> R<T> fail(ResponseCode responseCode) {
        return new R<>(responseCode.getCode(), responseCode.getMessage(), null);
    }

    public static <T> R<T> fail(ResponseCode responseCode, String message) {
        return new R<>(responseCode.getCode(), message, null);
    }

    public static <T> R<T> fail(int code, String message) {
        return new R<>(code, message, null);
    }

    public boolean isSuccess() {
        return code == ResponseCode.SUCCESS.getCode();
    }
}
