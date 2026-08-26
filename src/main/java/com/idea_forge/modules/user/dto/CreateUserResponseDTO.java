package com.idea_forge.modules.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserResponseDTO {
    private String name;
    private String email;
}
