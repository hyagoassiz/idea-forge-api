package com.idea_forge.modules.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BoardResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Boolean active;
}
