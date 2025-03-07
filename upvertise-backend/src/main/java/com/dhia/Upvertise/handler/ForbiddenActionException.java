package com.dhia.Upvertise.handler;

public class ForbiddenActionException extends BusinessException {

    public ForbiddenActionException(String message) {
        super(BusinessErrorCodes.UNAUTHORIZED_ACCESS, message);
    }
}
