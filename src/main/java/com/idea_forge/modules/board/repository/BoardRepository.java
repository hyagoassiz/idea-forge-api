package com.idea_forge.modules.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.idea_forge.modules.board.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

}
