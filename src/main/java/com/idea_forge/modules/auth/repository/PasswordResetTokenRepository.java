package com.idea_forge.modules.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.idea_forge.modules.auth.entity.PasswordResetToken;
import com.idea_forge.modules.user.entity.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);

    List<PasswordResetToken> findByUserAndUsedAtIsNull(User user);

    Optional<PasswordResetToken> findByTokenAndUsedAtIsNullAndExpiresAtAfter(String token, LocalDateTime now);

}
