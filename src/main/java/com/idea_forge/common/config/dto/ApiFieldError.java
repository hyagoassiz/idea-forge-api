package com.idea_forge.common.config.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiFieldError {

    private String field;
    private String code;
    private String message;
}
