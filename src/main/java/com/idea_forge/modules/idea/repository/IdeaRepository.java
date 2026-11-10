package com.idea_forge.modules.idea.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.idea_forge.modules.idea.entity.Idea;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

    @Query("""
            SELECT i
            FROM Idea i
            WHERE i.owner.id = :ownerId
              AND (:archived IS NULL OR i.archived = :archived)
              AND (
                  LOWER(i.name) LIKE LOWER(CONCAT('%', COALESCE(:search, ''), '%'))
                  OR LOWER(i.description) LIKE LOWER(CONCAT('%', COALESCE(:search, ''), '%'))
              )
            ORDER BY LOWER(i.name) ASC
            """)
    List<Idea> findAllByOwnerIdAndBoardId(Long ownerId, Long boardId, Boolean archived, String search);

    Optional<Idea> findByIdAndOwnerId(Long id, Long ownerId);

    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);

    boolean existsByOwnerIdAndNameIgnoreCaseAndIdNot(Long ownerId, String name, Long id);

}
