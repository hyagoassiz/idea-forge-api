package com.idea_forge.common.config.dto;

import java.time.Instant;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private int status;
    private String code;
    private String message;
    private List<ApiFieldError> errors;
    private String timestamp;

    public ApiErrorResponse(int status, String code, String message, List<ApiFieldError> errors) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.errors = errors;
        this.timestamp = Instant.now().toString();
    }
}
