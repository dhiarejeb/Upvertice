package com.dhia.Upvertise.handler;

public class BusinessException extends RuntimeException {
        private final BusinessErrorCodes errorCode;

    public BusinessException(BusinessErrorCodes errorCode) {
            super(errorCode.getDescription());
            this.errorCode = errorCode;
        }

    public BusinessException(BusinessErrorCodes errorCode, String message) {
            super(message);
            this.errorCode = errorCode;
        }

        public BusinessErrorCodes getErrorCode() {
            return errorCode;
        }
}
