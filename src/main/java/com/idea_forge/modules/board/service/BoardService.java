package com.idea_forge.modules.board.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idea_forge.common.exception.FieldValidationException;
import com.idea_forge.common.service.AuthenticatedUserService;
import com.idea_forge.modules.board.dto.BoardResponseDTO;
import com.idea_forge.modules.board.dto.CreateBoardRequestDTO;
import com.idea_forge.modules.board.dto.UpdateBoardRequestDTO;
import com.idea_forge.modules.board.entity.Board;
import com.idea_forge.modules.board.filter.BoardFilter;
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
    public List<BoardResponseDTO> getAllBoards(BoardFilter boardFilter) {
        Long userId = getAuthenticatedUser().getId();

        List<Board> boards = boardRepository.findAllByOwnerId(
                userId,
                boardFilter.getArchived(),
                boardFilter.getSearch());

        return boardMapper.toResponseList(boards);
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

    @Transactional
    public BoardResponseDTO archive(Long id) {
        return updateArchiveStatus(id, true);
    }

    @Transactional
    public BoardResponseDTO unarchive(Long id) {
        return updateArchiveStatus(id, false);
    }

    private BoardResponseDTO updateArchiveStatus(Long id, boolean archived) {
        Board board = getBoardOwnedByAuthenticatedUser(id);

        board.setArchived(archived);

        Board savedBoard = boardRepository.save(board);

        return boardMapper.toResponse(savedBoard);
    }

    private User getAuthenticatedUser() {
        return authenticatedUserService.getCurrentUser();
    }

    public Board getBoardOwnedByAuthenticatedUser(Long id) {
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