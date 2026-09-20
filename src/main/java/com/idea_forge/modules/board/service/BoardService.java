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
import com.idea_forge.modules.user.repository.UserRepository;

@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final BoardMapper boardMapper;

    public BoardService(
            BoardRepository boardRepository,
            UserRepository userRepository,
            AuthenticatedUserService authenticatedUserService,
            BoardMapper boardMapper) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.boardMapper = boardMapper;
    }

    @Transactional
    public BoardResponseDTO createBoard(CreateBoardRequestDTO request) {
        User authenticatedUser = authenticatedUserService.getCurrentUser();

        String normalizedName = normalize(request.getName());
        String normalizedDescription = normalize(request.getDescription());

        if (boardRepository.existsByOwnerAndNameIgnoreCase(
                authenticatedUser,
                normalizedName)) {
            throw new FieldValidationException(
                    "name",
                    "Já existe um quadro com esse nome");
        }

        Board board = boardMapper.toEntity(request);

        board.setName(normalizedName);
        board.setDescription(normalizedDescription);
        board.setOwner(authenticatedUser);

        Board savedBoard = boardRepository.save(board);

        authenticatedUser.setActiveBoard(savedBoard);
        userRepository.save(authenticatedUser);

        BoardResponseDTO response = boardMapper.toResponse(savedBoard);
        response.setActive(true);

        return response;
    }

    private String normalize(String value) {
        return value == null ? null : value.trim();
    }
}
