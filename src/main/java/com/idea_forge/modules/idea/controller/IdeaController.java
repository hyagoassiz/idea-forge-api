package com.idea_forge.modules.idea.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.idea_forge.modules.idea.dto.CreateIdeaRequestDTO;
import com.idea_forge.modules.idea.dto.IdeaResponseDTO;
import com.idea_forge.modules.idea.service.IdeaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/boards/{boardId}/ideas")
public class IdeaController {

    private final IdeaService ideaService;

    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdeaResponseDTO createIdea(
            @PathVariable Long boardId,
            @Valid @RequestBody CreateIdeaRequestDTO createIdeaRequestDTO) {
        return ideaService.createIdea(boardId, createIdeaRequestDTO);
    }

    @GetMapping
    public List<IdeaResponseDTO> getAllIdeas(@PathVariable Long boardId) {
        return ideaService.getAllIdeas(boardId);
    }

}
