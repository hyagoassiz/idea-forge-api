package com.idea_forge.modules.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.idea_forge.modules.board.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findAllByOwnerId(Long ownerId, Sort sort);

    Optional<Board> findByIdAndOwnerId(Long id, Long ownerId);

    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);

    boolean existsByOwnerIdAndNameIgnoreCaseAndIdNot(Long ownerId, String name, Long id);

}
