package com.dhia.Upvertise.handler;

public class UnauthorizedAccessException extends BusinessException {
    public UnauthorizedAccessException() {
        super(BusinessErrorCodes.UNAUTHORIZED_ACCESS);
    }

    public UnauthorizedAccessException(String message) {
        super(BusinessErrorCodes.UNAUTHORIZED_ACCESS, message);
    }
}
