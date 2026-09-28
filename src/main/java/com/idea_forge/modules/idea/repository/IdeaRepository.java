package com.idea_forge.modules.idea.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.idea_forge.modules.idea.entity.Idea;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

    boolean existsByOwnerIdAndNameIgnoreCase(Long ownerId, String name);

    boolean existsByOwnerIdAndNameIgnoreCaseAndIdNot(Long ownerId, String name, Long id);

}
