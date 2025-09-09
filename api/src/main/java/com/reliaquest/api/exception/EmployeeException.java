package com.reliaquest.api.exception;

/**
 * Custom unchecked exception for employee-related validation and business errors.
 */
public class EmployeeException extends RuntimeException {
    public EmployeeException(String message) {
        super(message);
    }
}
