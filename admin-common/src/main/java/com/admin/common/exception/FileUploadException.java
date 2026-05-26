package com.admin.common.exception;

import lombok.Getter;

/**
 * 文件上传异常
 */
@Getter
public class FileUploadException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final int code;

    public FileUploadException(String message) {
        super(message);
        this.code = 500;
    }

    public FileUploadException(int code, String message) {
        super(message);
        this.code = code;
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
}
