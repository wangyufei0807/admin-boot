package com.admin.common.exception;

import com.admin.common.enums.ResponseCode;
import lombok.Getter;

/**
 * 系统异常
 */
@Getter
public class SystemException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int code;

    public SystemException(String message) {
        super(message);
        this.code = ResponseCode.SYSTEM_ERROR.getCode();
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
        this.code = ResponseCode.SYSTEM_ERROR.getCode();
    }

    public SystemException(int code, String message) {
        super(message);
        this.code = code;
    }

    public SystemException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}
