package com.idea_forge.modules.idea.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.idea_forge.modules.idea.dto.CreateIdeaRequestDTO;
import com.idea_forge.modules.idea.dto.IdeaResponseDTO;
import com.idea_forge.modules.idea.dto.UpdateIdeaRequestDTO;
import com.idea_forge.modules.idea.dto.UpdateIdeaStatusRequestDTO;
import com.idea_forge.modules.idea.service.IdeaService;

import jakarta.validation.Valid;

@RestController
public class IdeaController {

    private final IdeaService ideaService;

    public IdeaController(IdeaService ideaService) {
        this.ideaService = ideaService;
    }

    @PostMapping("/boards/{boardId}/ideas")
    @ResponseStatus(HttpStatus.CREATED)
    public IdeaResponseDTO createIdea(
            @PathVariable Long boardId,
            @Valid @RequestBody CreateIdeaRequestDTO createIdeaRequestDTO) {
        return ideaService.createIdea(boardId, createIdeaRequestDTO);
    }

    @GetMapping("/boards/{boardId}/ideas")
    public List<IdeaResponseDTO> getAllIdeas(
            @PathVariable Long boardId) {
        return ideaService.getAllIdeas(boardId);
    }

    @PatchMapping("/ideas/{ideaId}/status")
    public IdeaResponseDTO updateStatus(
            @PathVariable Long ideaId,
            @Valid @RequestBody UpdateIdeaStatusRequestDTO updateIdeaStatusRequestDTO) {

        return ideaService.updateStatus(
                ideaId,
                updateIdeaStatusRequestDTO);
    }

    @GetMapping("/ideas/{ideaId}")
    public IdeaResponseDTO getIdeaById(
            @PathVariable Long ideaId) {
        return ideaService.getIdeaById(ideaId);
    }

    @PutMapping("/ideas/{id}")
    public IdeaResponseDTO updateIdea(@PathVariable Long ideaId,
            @Valid @RequestBody UpdateIdeaRequestDTO updateIdeaRequestDTO) {
        return ideaService.updateIdea(ideaId, updateIdeaRequestDTO);
    }

    @PatchMapping("/ideas/{id}/archive")
    public IdeaResponseDTO archive(@PathVariable Long ideaId) {
        return ideaService.archive(ideaId);
    }

    @PatchMapping("/ideas/{id}/unarchive")
    public IdeaResponseDTO unarchive(@PathVariable Long ideaId) {
        return ideaService.unarchive(ideaId);
    }
}
