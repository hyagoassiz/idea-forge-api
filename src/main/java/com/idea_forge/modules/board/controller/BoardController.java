package com.idea_forge.modules.board.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.idea_forge.modules.board.dto.BoardResponseDTO;
import com.idea_forge.modules.board.dto.CreateBoardRequestDTO;
import com.idea_forge.modules.board.dto.UpdateBoardRequestDTO;
import com.idea_forge.modules.board.service.BoardService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BoardResponseDTO createBoard(@Valid @RequestBody CreateBoardRequestDTO createBoardRequestDTO) {
        return boardService.createBoard(createBoardRequestDTO);
    }

    @GetMapping
    public List<BoardResponseDTO> getAllBoards() {
        return boardService.getAllBoards();
    }

    @GetMapping("/{id}")
    public BoardResponseDTO getBoardById(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }

    @PutMapping("/{id}")
    public BoardResponseDTO updateBoard(@PathVariable Long id,
            @Valid @RequestBody UpdateBoardRequestDTO updateBoardRequestDTO) {
        return boardService.updateBoard(id, updateBoardRequestDTO);
    }
}
