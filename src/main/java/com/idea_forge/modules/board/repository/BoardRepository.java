package com.idea_forge.modules.board.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.idea_forge.modules.board.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

        @Query("""
                        SELECT b
                        FROM Board b
                        WHERE b.owner.id = :ownerId
                          AND (:archived IS NULL OR b.archived = :archived)
                          AND (
                              LOWER(b.name) LIKE LOWER(CONCAT('%', COALESCE(:search, ''), '%'))
                              OR LOWER(b.description) LIKE LOWER(CONCAT('%', COALESCE(:search, ''), '%'))
                          )
                        ORDER BY LOWER(b.name) ASC
                        """)
        List<Board> findAllByOwnerId(
                        Long ownerId,
                        Boolean archived,
                        String search);

        Optional<Board> findByIdAndOwnerId(Long id, Long ownerId);

        boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);

        boolean existsByOwnerIdAndNameIgnoreCaseAndIdNot(Long ownerId, String name, Long id);

}
