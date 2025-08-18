package com.innov8ors.insurance.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum InsuranceServiceErrorType {
    INTERNAL_SERVER_ERROR("ERR_INSURANCE_000", "Unexpected server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHORIZED("ERR_INSURANCE_001", "Unauthorized request", HttpStatus.FORBIDDEN),
    NOT_FOUND("ERR_INSURANCE_002", "Entry not found", HttpStatus.NOT_FOUND),
    ALREADY_EXISTS("ERR_INSURANCE_003", "Entry already exists", HttpStatus.CONFLICT),
    BAD_REQUEST("ERR_INSURANCE_004", "Bad request", HttpStatus.BAD_REQUEST),
    COLLECTION_WRITE_ERROR("ERR_INSURANCE_005", "Error in writing to collection", HttpStatus.INTERNAL_SERVER_ERROR);

    private String errorCode;
    private String errorMessage;
    private HttpStatus httpStatus;
}
