package com.dhia.Upvertise.handler;



public class OperationNotPermittedException extends BusinessException {

    //Used when an action is not allowed due to business rules (e.g., wrong sponsorship status).

    public OperationNotPermittedException(String message) {
        super(BusinessErrorCodes.OPERATION_NOT_PERMITTED, message);
    }
}
