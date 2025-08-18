package com.innov8ors.insurance.exception;

import com.innov8ors.insurance.error.InsuranceServiceErrorType;

public class NotFoundException extends InsuranceServiceException {
    public NotFoundException(String message) {
        super(message, InsuranceServiceErrorType.NOT_FOUND);
    }
}
