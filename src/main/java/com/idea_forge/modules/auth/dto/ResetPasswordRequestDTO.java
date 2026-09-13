package com.idea_forge.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResetPasswordRequestDTO {

    @NotBlank(message = "Token é obrigatório")
    private String token;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 128, message = "Senha deve conter entre 8 e 128 caracteres")
    @Pattern(regexp = "^\\S+$", message = "Senha não pode conter espaços em branco")
    private String password;

}
