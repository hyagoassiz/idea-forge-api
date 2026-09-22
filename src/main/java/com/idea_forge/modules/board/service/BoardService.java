package com.idea_forge.modules.board.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idea_forge.common.exception.FieldValidationException;
import com.idea_forge.common.service.AuthenticatedUserService;
import com.idea_forge.modules.board.dto.BoardResponseDTO;
import com.idea_forge.modules.board.dto.CreateBoardRequestDTO;
import com.idea_forge.modules.board.dto.UpdateBoardRequestDTO;
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
        User authenticatedUser = getAuthenticatedUser();

        validateBoardName(createBoardRequestDTO.getName(), authenticatedUser);

        Board board = boardMapper.toEntity(createBoardRequestDTO);
        board.setOwner(authenticatedUser);

        Board savedBoard = boardRepository.save(board);

        return boardMapper.toResponse(savedBoard);
    }

    @Transactional(readOnly = true)
    public List<BoardResponseDTO> getAllBoards() {
        Long userId = getAuthenticatedUser().getId();

        return boardRepository
                .findAllByOwnerId(userId, Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(boardMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BoardResponseDTO getBoardById(Long id) {
        Board board = getBoardOwnedByAuthenticatedUser(id);

        return boardMapper.toResponse(board);
    }

    @Transactional
    public BoardResponseDTO updateBoard(
            Long id,
            UpdateBoardRequestDTO updateBoardRequestDTO) {

        Board board = getBoardOwnedByAuthenticatedUser(id);

        String newName = updateBoardRequestDTO.getName();

        if (!board.getName().equalsIgnoreCase(newName)) {
            validateBoardName(newName, board.getOwner(), id);
        }

        board.setName(newName);
        board.setDescription(updateBoardRequestDTO.getDescription());

        Board savedBoard = boardRepository.save(board);

        return boardMapper.toResponse(savedBoard);
    }

    private User getAuthenticatedUser() {
        return authenticatedUserService.getCurrentUser();
    }

    private Board getBoardOwnedByAuthenticatedUser(Long id) {
        Long userId = getAuthenticatedUser().getId();

        return boardRepository.findByIdAndOwnerId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Quadro não encontrado"));
    }

    private void validateBoardName(String name, User owner) {
        boolean nameTaken = boardRepository
                .existsByOwnerIdAndNameIgnoreCase(owner.getId(), name);

        if (nameTaken) {
            throwBoardNameAlreadyExists();
        }
    }

    private void validateBoardName(String name, User owner, Long boardId) {
        boolean nameTaken = boardRepository
                .existsByOwnerIdAndNameIgnoreCaseAndIdNot(
                        owner.getId(), name, boardId);

        if (nameTaken) {
            throwBoardNameAlreadyExists();
        }
    }

    private void throwBoardNameAlreadyExists() {
        throw new FieldValidationException(
                "name",
                "Já existe um quadro com esse nome");
    }
}