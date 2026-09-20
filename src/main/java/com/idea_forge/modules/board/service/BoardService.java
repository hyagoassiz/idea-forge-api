package com.idea_forge.modules.board.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idea_forge.common.exception.FieldValidationException;
import com.idea_forge.common.service.AuthenticatedUserService;
import com.idea_forge.modules.board.dto.BoardResponseDTO;
import com.idea_forge.modules.board.dto.CreateBoardRequestDTO;
import com.idea_forge.modules.board.entity.Board;
import com.idea_forge.modules.board.mapper.BoardMapper;
import com.idea_forge.modules.board.repository.BoardRepository;
import com.idea_forge.modules.user.entity.User;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    private final AuthenticatedUserService authenticatedUserService;

    private final BoardMapper boardMapper;

    public BoardService(
            BoardRepository boardRepository,
            AuthenticatedUserService authenticatedUserService,
            BoardMapper boardMapper) {
        this.boardRepository = boardRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.boardMapper = boardMapper;
    }

    @Transactional
    public BoardResponseDTO createBoard(CreateBoardRequestDTO createBoardRequestDTO) {

        User authenticatedUser = authenticatedUserService.getCurrentUser();

        boolean boardAlreadyExists = boardRepository
                .existsByOwnerIdAndNameIgnoreCase(
                        authenticatedUser.getId(),
                        createBoardRequestDTO.getName());

        if (boardAlreadyExists) {
            throw new FieldValidationException(
                    "name",
                    "Já existe um quadro com esse nome");
        }

        Board board = boardMapper.toEntity(createBoardRequestDTO);
        board.setOwner(authenticatedUser);

        Board savedBoard = boardRepository.save(board);

        return boardMapper.toResponse(savedBoard);
    }
}