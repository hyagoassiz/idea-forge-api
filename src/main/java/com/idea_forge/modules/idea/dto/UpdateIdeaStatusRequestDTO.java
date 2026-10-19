package com.idea_forge.modules.idea.dto;

import com.idea_forge.modules.idea.enums.IdeaStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateIdeaStatusRequestDTO {

    @NotNull(message = "Status é obrigatório")
    private IdeaStatus status;

}
