package com.idea_forge.modules.idea.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idea_forge.common.exception.FieldValidationException;
import com.idea_forge.common.service.AuthenticatedUserService;
import com.idea_forge.modules.board.entity.Board;
import com.idea_forge.modules.board.service.BoardService;
import com.idea_forge.modules.idea.dto.CreateIdeaRequestDTO;
import com.idea_forge.modules.idea.dto.IdeaResponseDTO;
import com.idea_forge.modules.idea.dto.UpdateIdeaRequestDTO;
import com.idea_forge.modules.idea.dto.UpdateIdeaStatusRequestDTO;
import com.idea_forge.modules.idea.entity.Idea;
import com.idea_forge.modules.idea.enums.IdeaStatus;
import com.idea_forge.modules.idea.mapper.IdeaMapper;
import com.idea_forge.modules.idea.repository.IdeaRepository;
import com.idea_forge.modules.user.entity.User;

@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final AuthenticatedUserService authenticatedUserService;
    private final BoardService boardService;
    private final IdeaMapper ideaMapper;

    public IdeaService(
            IdeaRepository ideaRepository,
            AuthenticatedUserService authenticatedUserService,
            BoardService boardService,
            IdeaMapper ideaMapper) {

        this.ideaRepository = ideaRepository;
        this.authenticatedUserService = authenticatedUserService;
        this.boardService = boardService;
        this.ideaMapper = ideaMapper;
    }

    @Transactional
    public IdeaResponseDTO createIdea(
            Long boardId,
            CreateIdeaRequestDTO createIdeaRequestDTO) {
        User authenticatedUser = getAuthenticatedUser();

        Board board = boardService.getBoardOwnedByAuthenticatedUser(boardId);

        validateBoardIsNotArchived(board);

        validateIdeaName(
                createIdeaRequestDTO.getName(),
                authenticatedUser);

        Idea idea = ideaMapper.toEntity(createIdeaRequestDTO);

        idea.setOwner(authenticatedUser);
        idea.setBoard(board);
        idea.setStatus(IdeaStatus.DRAFT);

        Idea savedIdea = ideaRepository.save(idea);

        return ideaMapper.toResponse(savedIdea);
    }

    @Transactional
    public IdeaResponseDTO updateIdea(
            Long ideaId,
            UpdateIdeaRequestDTO updateIdeaRequestDTO) {

        Idea idea = getIdeaOwnedByAuthenticatedUser(ideaId);

        validateBoardIsNotArchived(idea.getBoard());

        String newName = updateIdeaRequestDTO.getName();

        if (!idea.getName().equalsIgnoreCase(newName)) {
            validateIdeaName(newName, idea.getOwner());
        }

        idea.setName(newName);
        idea.setDescription(updateIdeaRequestDTO.getDescription());

        Idea savedIdea = ideaRepository.save(idea);

        return ideaMapper.toResponse(savedIdea);
    }

    @Transactional(readOnly = true)
    public List<IdeaResponseDTO> getAllIdeas(Long boardId) {
        Long userId = getAuthenticatedUser().getId();

        return ideaRepository
                .findAllByOwnerIdAndBoardId(userId, boardId)
                .stream()
                .map(ideaMapper::toResponse)
                .toList();
    }

    @Transactional
    public IdeaResponseDTO updateStatus(Long ideaId,
            UpdateIdeaStatusRequestDTO updateIdeaStatusRequestDTO) {
        Idea idea = getIdeaOwnedByAuthenticatedUser(ideaId);

        Board board = boardService.getBoardOwnedByAuthenticatedUser(idea.getBoard().getId());

        validateBoardIsNotArchived(board);

        idea.setStatus(updateIdeaStatusRequestDTO.getStatus());

        Idea savedIdea = ideaRepository.save(idea);

        return ideaMapper.toResponse(savedIdea);
    }

    @Transactional
    public IdeaResponseDTO archive(Long ideaId) {
        return updateArchiveStatus(ideaId, true);
    }

    @Transactional
    public IdeaResponseDTO unarchive(Long ideaId) {
        return updateArchiveStatus(ideaId, false);
    }

    private IdeaResponseDTO updateArchiveStatus(Long ideaId, boolean archived) {

        Idea idea = getIdeaOwnedByAuthenticatedUser(ideaId);

        validateBoardIsNotArchived(idea.getBoard());

        idea.setArchived(archived);

        Idea savedIdea = ideaRepository.save(idea);

        return ideaMapper.toResponse(savedIdea);
    }

    @Transactional(readOnly = true)
    public IdeaResponseDTO getIdeaById(Long ideaId) {
        Idea idea = getIdeaOwnedByAuthenticatedUser(ideaId);

        return ideaMapper.toResponse(idea);
    }

    public Idea getIdeaOwnedByAuthenticatedUser(Long ideaId) {
        Long userId = getAuthenticatedUser().getId();

        return ideaRepository.findByIdAndOwnerId(ideaId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Ideia não encontrada"));
    }

    private User getAuthenticatedUser() {
        return authenticatedUserService.getCurrentUser();
    }

    private void validateBoardIsNotArchived(Board board) {
        if (board.isArchived()) {
            throw new FieldValidationException(
                    "boardId",
                    "Não é possível realizar esta operação em um quadro arquivado");
        }
    }

    private void validateIdeaName(String name, User owner) {
        boolean nameTaken = ideaRepository
                .existsByOwnerIdAndNameIgnoreCase(owner.getId(), name);

        if (nameTaken) {
            throwBoardNameAlreadyExists();
        }
    }

    private void throwBoardNameAlreadyExists() {
        throw new FieldValidationException(
                "name",
                "Já existe uma ideia com esse nome");
    }

}
