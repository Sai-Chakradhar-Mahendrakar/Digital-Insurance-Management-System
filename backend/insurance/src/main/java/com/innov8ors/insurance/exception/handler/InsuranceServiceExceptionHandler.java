package com.innov8ors.insurance.exception.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.innov8ors.insurance.error.InsuranceServiceError;
import com.innov8ors.insurance.error.InsuranceServiceErrorType;
import com.innov8ors.insurance.exception.AlreadyExistsException;
import com.innov8ors.insurance.exception.BadRequestException;
import com.innov8ors.insurance.exception.InsuranceServiceException;
import com.innov8ors.insurance.exception.NotFoundException;
import com.innov8ors.insurance.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static com.innov8ors.insurance.error.InsuranceServiceErrorType.ALREADY_EXISTS;
import static com.innov8ors.insurance.error.InsuranceServiceErrorType.BAD_REQUEST;
import static com.innov8ors.insurance.error.InsuranceServiceErrorType.INTERNAL_SERVER_ERROR;
import static com.innov8ors.insurance.error.InsuranceServiceErrorType.NOT_FOUND;
import static com.innov8ors.insurance.util.Constant.CORE_EXCEPTION_NAME;

@ControllerAdvice
public class InsuranceServiceExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<InsuranceServiceError> handleGenericException(Exception ex) {
        return handleException(ex, INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getErrorMessage(), INTERNAL_SERVER_ERROR.getHttpStatus());
    }

    @ExceptionHandler(value = {UnsatisfiedServletRequestParameterException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<InsuranceServiceError> handleRequestValidationErrors(Exception ex) {
        return handleException(ex, BAD_REQUEST, BAD_REQUEST.getErrorMessage(), BAD_REQUEST.getHttpStatus());
    }

    @ExceptionHandler(value = {HttpMessageNotReadableException.class})
    public ResponseEntity<InsuranceServiceError> handleHttpMsgNotReadableException(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException) {
            return handleInvalidFormatException((InvalidFormatException) ex.getCause());
        }
        return handleException(ex, BAD_REQUEST, BAD_REQUEST.getErrorMessage(), BAD_REQUEST.getHttpStatus());
    }

    @ExceptionHandler(value = {InvalidFormatException.class})
    public ResponseEntity<InsuranceServiceError> handleInvalidFormatException(InvalidFormatException ifx) {
        Map<String, Object> additionalInfo = new TreeMap<>();
        if (ifx.getTargetType() != null && ifx.getTargetType().isEnum()) {
            additionalInfo.put("violation 1", String.format("Invalid enum value: '%s' for the field: '%s'. The value must be one of: %s.",
                    ifx.getValue(), ifx.getPath().get(ifx.getPath().size() - 1).getFieldName(), Arrays.toString(ifx.getTargetType().getEnumConstants())));
        }
        return handleException(ifx, BAD_REQUEST, BAD_REQUEST.getErrorMessage(), BAD_REQUEST.getHttpStatus(), Optional.of(additionalInfo));
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<InsuranceServiceError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, Object> additionalInfo = new TreeMap<>();
        List<String> messages = new ArrayList<>();
        List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrorList) {
            messages.add(String.format("%s => %s", fieldError.getField(), fieldError.getDefaultMessage()));
        }
        Collections.sort(messages);
        for (int i = 0; i < messages.size(); i++) {
            additionalInfo.put("violation " + (i + 1), messages.get(i));
        }
        return handleException(ex, BAD_REQUEST, BAD_REQUEST.getErrorMessage(), BAD_REQUEST.getHttpStatus(), Optional.of(additionalInfo));
    }

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<InsuranceServiceError> handleNotFoundException(NotFoundException ex) {
        return handleException(ex, NOT_FOUND, ex.getMessage(), NOT_FOUND.getHttpStatus(), Optional.ofNullable(ex.getAdditionalAttributes()));
    }

    @ExceptionHandler(value = AlreadyExistsException.class)
    public ResponseEntity<InsuranceServiceError> handleAlreadyExistsException(AlreadyExistsException ex) {
        return handleException(ex, ALREADY_EXISTS, ex.getMessage(), ALREADY_EXISTS.getHttpStatus(), Optional.ofNullable(ex.getAdditionalAttributes()));
    }

    @ExceptionHandler(value = BadRequestException.class)
    public ResponseEntity<InsuranceServiceError> handleBadRequestException(BadRequestException ex) {
        return handleException(ex, BAD_REQUEST, ex.getMessage(), BAD_REQUEST.getHttpStatus(), Optional.ofNullable(ex.getAdditionalAttributes()));
    }

    @ExceptionHandler(value = InsuranceServiceException.class)
    public ResponseEntity<InsuranceServiceError> handleInsuranceServiceException(InsuranceServiceException ex) {
        return handleException(ex, ex.getErrorType(), ex.getErrorType().getErrorMessage(), ex.getErrorType().getHttpStatus());
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public ResponseEntity<InsuranceServiceError> handleUnauthorizedException(UnauthorizedException ex) {
        return handleException(ex, ex.getErrorType(), ex.getMessage(), ex.getErrorType().getHttpStatus(),
                Optional.ofNullable(ex.getAdditionalAttributes()));
    }

    private ResponseEntity<InsuranceServiceError> handleException(Exception ex,
                                                                  InsuranceServiceErrorType type,
                                                                  String errorTitle,
                                                                  HttpStatus httpStatus) {
        return handleException(ex, type, errorTitle, httpStatus, Optional.empty());
    }

    private ResponseEntity<InsuranceServiceError> handleException(Exception ex,
                                                                  InsuranceServiceErrorType type,
                                                                  String errorTitle,
                                                                  HttpStatus httpStatus,
                                                                  Optional<Map<String, Object>> additionalInfo) {
        errorTitle = errorTitle == null ? type.getErrorMessage() : errorTitle;

        InsuranceServiceError.InsuranceServiceErrorBuilder errorBuilder = InsuranceServiceError.builder()
                .errorType(type)
                .errorCode(type.getErrorCode())
                .traceId(null)
                .errorMessage(errorTitle)
                .additionalInfo(additionalInfo.orElse(null));

        additionalInfo.ifPresent(errorBuilder::additionalInfo);

        InsuranceServiceError error = errorBuilder.build();
        Map<String, Object> exceptionAttrs = getExceptionAttrs(ex);

        return new ResponseEntity<>(error, httpStatus);
    }

    private Map<String, Object> getExceptionAttrs(Exception ex) {
        Map<String, Object> exceptionAttrs = new HashMap<>();

        if (!(ex instanceof InsuranceServiceException)) {
            return exceptionAttrs;
        }

        InsuranceServiceException parsedException = (InsuranceServiceException) ex;
        exceptionAttrs.putAll(parsedException.getAdditionalAttributes());

        Optional<String> coreExceptionNameMaybe = getCoreExceptionName(ex);
        coreExceptionNameMaybe.ifPresent(s -> exceptionAttrs.put(CORE_EXCEPTION_NAME, s));

        return exceptionAttrs;
    }

    private Optional<String> getCoreExceptionName(Exception ex) {
        if (!(ex instanceof InsuranceServiceException)) {
            return Optional.empty();
        }

        InsuranceServiceException parsedException = (InsuranceServiceException) ex;
        if (parsedException.getCause() == null) {
            return Optional.empty();
        }

        return Optional.of(parsedException.getCause().getClass().getName());
    }
}
