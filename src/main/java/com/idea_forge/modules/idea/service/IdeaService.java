package com.idea_forge.modules.idea.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idea_forge.common.exception.FieldValidationException;
import com.idea_forge.common.service.AuthenticatedUserService;
import com.idea_forge.modules.board.entity.Board;
import com.idea_forge.modules.board.service.BoardService;
import com.idea_forge.modules.idea.dto.CreateIdeaRequestDTO;
import com.idea_forge.modules.idea.dto.IdeaResponseDTO;
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

    private User getAuthenticatedUser() {
        return authenticatedUserService.getCurrentUser();
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
