package com.trevix.property_management.exception;

import com.trevix.property_management.enums.ErrorCode;

public class AppException extends RuntimeException {
    public final ErrorCode errorCode;

    public AppException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}