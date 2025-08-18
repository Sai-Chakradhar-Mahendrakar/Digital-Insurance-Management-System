package com.innov8ors.insurance.exception;

import com.innov8ors.insurance.error.InsuranceServiceErrorType;

public class UnauthorizedException extends InsuranceServiceException {
    public UnauthorizedException(String message, InsuranceServiceErrorType errorType) {
        super(message, errorType);
    }
}
