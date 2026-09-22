package com.idea_forge.modules.board.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.idea_forge.modules.board.dto.BoardResponseDTO;
import com.idea_forge.modules.board.dto.CreateBoardRequestDTO;
import com.idea_forge.modules.board.entity.Board;

@Mapper(componentModel = "spring")
public interface BoardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "owner", ignore = true)
    Board toEntity(CreateBoardRequestDTO createBoardRequestDTO);

    BoardResponseDTO toResponse(Board board);
}
