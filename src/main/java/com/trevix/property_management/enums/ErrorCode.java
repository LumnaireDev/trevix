package com.trevix.property_management.enums;

public enum ErrorCode {
    RESOURCE_NOT_FOUND(404, "RESOURCE_NOT_FOUND"),
    DUPLICATE_RESOURCE(409, "DUPLICATE_RESOURCE"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),
    BAD_REQUEST(400, "BAD_REQUEST"),
    INTERNAL_ERROR(500, "INTERNAL_ERROR");

    public final int status;
    public final String code;

    ErrorCode(int status, String code) {
        this.status = status;
        this.code = code;
    }
}
