package com.innov8ors.insurance.error;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Builder
@Getter
public class InsuranceServiceError {
    private String traceId;
    private String errorCode;
    private InsuranceServiceErrorType errorType;
    private String errorMessage;
    private Map<String, Object> additionalInfo;
}
