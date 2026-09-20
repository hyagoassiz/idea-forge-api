package com.idea_forge.modules.board.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.idea_forge.modules.board.entity.Board;
import com.idea_forge.modules.user.entity.User;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    boolean existsByOwnerAndNameIgnoreCase(User owner, String name);
}
