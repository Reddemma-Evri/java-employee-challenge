package com.reliaquest.api.exception;

import com.reliaquest.api.dto.ErrorDTO;
import com.reliaquest.api.dto.ValidationErrorDTO;
import com.reliaquest.api.model.ApiResponse;
import com.reliaquest.api.utils.AppUtils;
import jakarta.validation.ConstraintViolationException;
import java.util.*;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@ControllerAdvice
public class EmployeeExceptionHandler {

    private final MessageSource messageSource;

    public EmployeeExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(EmployeeException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmployeeException(EmployeeException ex) {
        ApiResponse<Void> response = AppUtils.buildResponse(
                null, messageSource, true, HttpStatus.INTERNAL_SERVER_ERROR, "employee.error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception ex) {
        ApiResponse<Void> response = AppUtils.buildResponse(
                null, messageSource, true, HttpStatus.INTERNAL_SERVER_ERROR, "generic.error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(TooManyRequestsException.class)
    public ResponseEntity<ApiResponse<Void>> handleTooManyRequestsException(TooManyRequestsException ex) {
        ApiResponse<Void> response = AppUtils.buildResponse(
                null, messageSource, true, HttpStatus.TOO_MANY_REQUESTS, "too.many.requests", ex.getMessage());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(response);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiResponse<List<ValidationErrorDTO>>> handleConstraintViolation(Exception ex) {
        ApiResponse<List<ValidationErrorDTO>> response = AppUtils.buildResponse(
                AppUtils.getValidationErrors(ex, messageSource),
                messageSource,
                true,
                HttpStatus.BAD_REQUEST,
                "validation.failed",
                new Object[] {AppUtils.getValidationErrors(ex, messageSource).size()});
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RemoteApiException.class)
    public ResponseEntity<ErrorDTO> handleRemoteApiException(RemoteApiException ex) {
        ex.printStackTrace();
        ApiResponse<ErrorDTO> apiResponse = ex.getApiResponse();
        return ResponseEntity.status(apiResponse.statusCode())
                .body(apiResponse.data().get());
    }
}
