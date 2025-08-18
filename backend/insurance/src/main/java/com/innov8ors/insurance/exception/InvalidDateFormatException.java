package com.innov8ors.insurance.exception;


import com.innov8ors.insurance.error.InsuranceServiceErrorType;

public class InvalidDateFormatException extends InsuranceServiceException {
    public InvalidDateFormatException(String message, Throwable cause) {
        super(message, cause, InsuranceServiceErrorType.BAD_REQUEST);
    }
}
