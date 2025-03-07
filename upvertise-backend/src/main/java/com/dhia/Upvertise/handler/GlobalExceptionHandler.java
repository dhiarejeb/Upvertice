package com.dhia.Upvertise.handler;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .status(ex.getErrorCode().getHttpStatus())
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(ex.getErrorCode().getCode())
                                .businessErrorDescription(ex.getErrorCode().getDescription())
                                .error(ex.getMessage())
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleGenericException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ExceptionResponse.builder()
                                .businessErrorCode(0)
                                .businessErrorDescription("Unexpected error occurred, please contact the admin")
                                .error(ex.getMessage())
                                .build()
                );
    }
}
