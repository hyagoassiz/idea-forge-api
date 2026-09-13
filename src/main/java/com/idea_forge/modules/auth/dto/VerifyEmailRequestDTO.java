package com.idea_forge.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class VerifyEmailRequestDTO {

    @NotBlank(message = "Token é obrigatório")
    private String token;
}
