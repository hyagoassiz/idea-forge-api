package com.idea_forge.modules.idea.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.idea_forge.modules.idea.entity.Idea;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findAllByOwnerIdAndBoardId(Long ownerId, Long boardId);

    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);

    boolean existsByOwnerIdAndNameIgnoreCaseAndIdNot(Long ownerId, String name, Long id);

}
