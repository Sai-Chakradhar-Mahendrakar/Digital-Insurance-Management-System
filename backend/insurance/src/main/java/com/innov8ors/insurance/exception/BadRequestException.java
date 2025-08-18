package com.innov8ors.insurance.exception;


import com.innov8ors.insurance.error.InsuranceServiceErrorType;

public class BadRequestException extends InsuranceServiceException {
    public BadRequestException(String message, Throwable cause) {
        super(message, cause, InsuranceServiceErrorType.BAD_REQUEST);
    }

    public BadRequestException(String message) {
        super(message, InsuranceServiceErrorType.BAD_REQUEST);
    }
}
