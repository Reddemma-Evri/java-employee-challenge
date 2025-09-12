package com.reliaquest.api.utils;

import com.reliaquest.api.dto.ValidationErrorDTO;
import com.reliaquest.api.model.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

public final class AppUtils {

    private AppUtils() {}

    public static <T> ApiResponse<T> buildResponse(
            T data, MessageSource messageSource, boolean error, HttpStatus status, String messageKey, Object... args) {
        String localizedMessage = messageSource.getMessage(messageKey, args, LocaleContextHolder.getLocale());
        return new ApiResponse<>(Optional.ofNullable(data), error, status.value(), localizedMessage);
    }

    public static List<ValidationErrorDTO> getValidationErrors(Exception ex, MessageSource messageSource) {
        Stream<ValidationErrorDTO> stream = Stream.empty();
        if (ex instanceof ConstraintViolationException cve) {
            stream = cve.getConstraintViolations().stream()
                    .map(v -> new ValidationErrorDTO(v.getPropertyPath().toString(), v.getMessage()));
        } else if (ex instanceof MethodArgumentNotValidException manve) {
            stream = manve.getBindingResult().getFieldErrors().stream()
                    .map(f -> new ValidationErrorDTO(
                            f.getField(), messageSource.getMessage(f, LocaleContextHolder.getLocale())));
        }

        return stream.toList();
    }
}
