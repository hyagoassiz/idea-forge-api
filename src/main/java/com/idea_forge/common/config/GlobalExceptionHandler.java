package com.idea_forge.common.config;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.idea_forge.common.config.dto.ApiErrorResponse;
import com.idea_forge.common.config.dto.ApiFieldError;
import com.idea_forge.common.exception.EmailAlreadyExistsException;
import com.idea_forge.common.exception.EmailAlreadyVerifiedException;
import com.idea_forge.common.exception.ExpiredVerificationTokenException;
import com.idea_forge.common.exception.FieldValidationException;
import com.idea_forge.common.exception.InvalidCredentialsException;
import com.idea_forge.common.exception.InvalidVerificationTokenException;
import com.idea_forge.common.exception.VerificationTokenAlreadyUsedException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        List<ApiFieldError> errors = new ArrayList<>();
        errors.add(new ApiFieldError("email", "EMAIL_ALREADY_EXISTS", ex.getMessage()));

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "EMAIL_ALREADY_EXISTS",
                ex.getMessage(),
                errors);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<ApiFieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::fieldErrorToApiFieldError)
                .collect(Collectors.toList());

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                "Dados de usuário inválidos",
                errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        List<ApiFieldError> errors = new ArrayList<>();
        errors.add(new ApiFieldError("general", "INVALID_CREDENTIALS", ex.getMessage()));

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "INVALID_CREDENTIALS",
                ex.getMessage(),
                errors);

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleFieldValidation(FieldValidationException ex) {
        List<ApiFieldError> errors = new ArrayList<>();
        errors.add(new ApiFieldError(ex.getField(), "ILLEGAL_ARGUMENT", ex.getMessage()));

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidVerificationTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidVerificationToken(InvalidVerificationTokenException ex) {
        List<ApiFieldError> errors = new ArrayList<>();
        errors.add(new ApiFieldError("token", "INVALID_VERIFICATION_TOKEN", ex.getMessage()));

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_VERIFICATION_TOKEN",
                ex.getMessage(),
                errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(EmailAlreadyVerifiedException.class)
    public ResponseEntity<ApiErrorResponse> handleEmailAlreadyVerified(EmailAlreadyVerifiedException ex) {
        List<ApiFieldError> errors = new ArrayList<>();
        errors.add(new ApiFieldError("email", "EMAIL_ALREADY_VERIFIED", ex.getMessage()));

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "EMAIL_ALREADY_VERIFIED",
                ex.getMessage(),
                errors);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(VerificationTokenAlreadyUsedException.class)
    public ResponseEntity<ApiErrorResponse> handleVerificationTokenAlreadyUsed(
            VerificationTokenAlreadyUsedException ex) {
        List<ApiFieldError> errors = new ArrayList<>();
        errors.add(new ApiFieldError("token", "VERIFICATION_TOKEN_ALREADY_USED", ex.getMessage()));

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.CONFLICT.value(),
                "VERIFICATION_TOKEN_ALREADY_USED",
                ex.getMessage(),
                errors);

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ExpiredVerificationTokenException.class)
    public ResponseEntity<ApiErrorResponse> handleExpiredVerificationToken(ExpiredVerificationTokenException ex) {
        List<ApiFieldError> errors = new ArrayList<>();
        errors.add(new ApiFieldError("token", "VERIFICATION_TOKEN_EXPIRED", ex.getMessage()));

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "VERIFICATION_TOKEN_EXPIRED",
                ex.getMessage(),
                errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        List<ApiFieldError> errors = new ArrayList<>();
        errors.add(new ApiFieldError("general", "ILLEGAL_ARGUMENT", ex.getMessage()));

        ApiErrorResponse response = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "ILLEGAL_ARGUMENT",
                ex.getMessage(),
                errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ApiFieldError fieldErrorToApiFieldError(FieldError fieldError) {
        String code = extractErrorCode(fieldError);
        return new ApiFieldError(
                fieldError.getField(),
                code,
                fieldError.getDefaultMessage());
    }

    private String extractErrorCode(FieldError fieldError) {
        String annotation = fieldError.getCode();

        if (annotation == null || annotation.isEmpty()) {
            return "VALIDATION_ERROR";
        }

        return switch (annotation) {
            case "NotBlank" -> "FIELD_REQUIRED";
            case "Email" -> "EMAIL_INVALID";
            case "Pattern" -> "PATTERN_INVALID";
            case "Size" -> "SIZE_INVALID";
            case "NotNull" -> "FIELD_REQUIRED";
            case "Min" -> "MIN_VALUE_INVALID";
            case "Max" -> "MAX_VALUE_INVALID";
            default -> annotation;
        };
    }
}
