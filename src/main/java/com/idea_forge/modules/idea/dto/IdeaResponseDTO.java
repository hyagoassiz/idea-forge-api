package com.idea_forge.modules.idea.dto;

import com.idea_forge.modules.board.dto.BoardResponseDTO;
import com.idea_forge.modules.idea.enums.IdeaStatus;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IdeaResponseDTO {
    private Long id;
    private String name;
    private String description;
    private IdeaStatus status;
    private BoardResponseDTO board;

}
