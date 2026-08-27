package com.idea_forge.modules.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateUserRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 100, message = "Nome deve conter entre 3 e 100 caracteres")
    @Pattern(regexp = "^(?!\\s)(?!.*\\s{2})(?!.*\\s$).*$", message = "Nome não pode começar ou terminar com espaço, nem conter espaços duplos")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    @Pattern(regexp = "^\\S+$", message = "Email não pode conter espaços vazios")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 8, max = 128, message = "Senha deve conter entre 8 e 128 caracteres")
    @Pattern(regexp = "^\\S+$", message = "Senha não pode conter espaços em branco")
    private String password;
}
