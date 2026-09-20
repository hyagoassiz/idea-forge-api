package com.idea_forge.modules.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateBoardRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    @Size(min = 3, max = 60, message = "Nome deve conter entre 3 e 60 caracteres")
    @Pattern(regexp = "^(?!\\s)(?!.*\\s{2})(?!.*\\s$).*$", message = "Nome não pode começar ou terminar com espaço, nem conter espaços duplos")
    private String name;

    @Size(max = 255, message = "Descrição deve conter no máximo 255 caracteres")
    @Pattern(regexp = "^(?!\\s)(?!.*\\s{2})(?!.*\\s$).*$", message = "Descrição não pode começar ou terminar com espaço, nem conter espaços duplos")
    private String description;
}
