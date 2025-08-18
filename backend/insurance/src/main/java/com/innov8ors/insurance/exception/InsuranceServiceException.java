package com.innov8ors.insurance.exception;

import com.innov8ors.insurance.error.InsuranceServiceErrorType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Getter
public class InsuranceServiceException extends RuntimeException {
    private final Map<String, Object> additionalAttributes = new HashMap<>();
    private final InsuranceServiceErrorType errorType;

    public InsuranceServiceException(String message, Throwable cause, InsuranceServiceErrorType errorType) {
        super(message, cause);
        this.errorType = errorType;
    }

    public InsuranceServiceException(String message, InsuranceServiceErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public InsuranceServiceException(InsuranceServiceErrorType errorType, Throwable exp) {
        super(exp);
        this.errorType = errorType;
    }

    public InsuranceServiceException addAttributes(Map<String, Object> attributes) {
        if (Objects.isNull(attributes)) return this;
        this.additionalAttributes.putAll(attributes);
        return this;
    }
}
