package com.idea_forge.modules.idea.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.idea_forge.modules.idea.dto.CreateIdeaRequestDTO;
import com.idea_forge.modules.idea.dto.IdeaResponseDTO;
import com.idea_forge.modules.idea.entity.Idea;

@Mapper(componentModel = "spring")
public interface IdeaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Idea toEntity(CreateIdeaRequestDTO createIdeaRequestDTO);

    IdeaResponseDTO toResponse(Idea idea);

}
