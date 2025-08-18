package com.innov8ors.insurance.exception;

import com.innov8ors.insurance.error.InsuranceServiceErrorType;

public class AlreadyExistsException extends InsuranceServiceException {
    public AlreadyExistsException(String message) {
        super(message, InsuranceServiceErrorType.ALREADY_EXISTS.ALREADY_EXISTS);
    }
}
