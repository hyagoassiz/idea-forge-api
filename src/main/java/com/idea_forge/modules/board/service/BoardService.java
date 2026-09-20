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

    @Transactional(readOnly = true)
    public List<BoardResponseDTO> getAllBoards() {
        return boardRepository.findAll(Sort.by(Sort.Direction.ASC, "name"))
                .stream()
                .map(boardMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public BoardResponseDTO getBoardById(Long id) {
        User authenticatedUser = authenticatedUserService.getCurrentUser();

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quadro não encontrado"));

        if (!board.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new com.idea_forge.common.exception.InvalidCredentialsException(
                    "Usuário não autorizado a visualizar esse quadro");
        }

        return boardMapper.toResponse(board);
    }

    @Transactional
    public BoardResponseDTO updateBoard(Long id, UpdateBoardRequestDTO updateBoardRequestDTO) {
        User authenticatedUser = authenticatedUserService.getCurrentUser();

        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quadro não encontrado"));

        if (!board.getOwner().getId().equals(authenticatedUser.getId())) {
            throw new com.idea_forge.common.exception.InvalidCredentialsException(
                    "Usuário não autorizado a editar esse quadro");
        }

        String newName = updateBoardRequestDTO.getName();

        if (!board.getName().equalsIgnoreCase(newName)) {
            boolean nameTaken = boardRepository.existsByOwnerIdAndNameIgnoreCaseAndIdNot(
                    authenticatedUser.getId(), newName, id);
            if (nameTaken) {
                throw new FieldValidationException("name", "Já existe um quadro com esse nome");
            }
        }

        board.setName(updateBoardRequestDTO.getName());
        board.setDescription(updateBoardRequestDTO.getDescription());

        Board saved = boardRepository.save(board);

        return boardMapper.toResponse(saved);
    }
}