package com.idea_forge.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ForgotPasswordResponseDTO {

    private String message;
    private String token;

    public ForgotPasswordResponseDTO(String message) {
        this.message = message;
    }

}
