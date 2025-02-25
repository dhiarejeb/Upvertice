package com.dhia.Upvertise.handler;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum BusinessErrorCodes {
    NO_CODE(0, HttpStatus.NOT_IMPLEMENTED, "No code"),
    INVALID_SPONSORSHIP_STATUS(100, HttpStatus.BAD_REQUEST, "Invalid sponsorship status for this operation"),
    UNAUTHORIZED_ACCESS(101, HttpStatus.FORBIDDEN, "You are not authorized to perform this action"),
    ENTITY_NOT_FOUND(102, HttpStatus.NOT_FOUND, "Requested entity not found"),
    DUPLICATE_ENTRY(103, HttpStatus.CONFLICT, "Duplicate entry detected"),
    OPERATION_NOT_PERMITTED(104, HttpStatus.BAD_REQUEST, "Operation not permitted"),
    INVALID_SUPPLIER_OFFER_STATUS(105, HttpStatus.BAD_REQUEST, "Invalid supplier offer status for this operation");

    @Getter
    private final int code;
    @Getter
    private final String description;
    @Getter
    private final HttpStatus httpStatus;

    BusinessErrorCodes(int code, HttpStatus status, String description) {
        this.code = code;
        this.description = description;
        this.httpStatus = status;
    }
}
