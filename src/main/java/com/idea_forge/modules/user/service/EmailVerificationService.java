package com.idea_forge.modules.user.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idea_forge.common.exception.EmailAlreadyVerifiedException;
import com.idea_forge.common.exception.ExpiredVerificationTokenException;
import com.idea_forge.common.exception.InvalidVerificationTokenException;
import com.idea_forge.common.exception.VerificationTokenAlreadyUsedException;
import com.idea_forge.modules.user.dto.EmailVerificationResponseDTO;
import com.idea_forge.modules.user.dto.VerifyEmailRequestDTO;
import com.idea_forge.modules.user.entity.EmailVerificationToken;
import com.idea_forge.modules.user.entity.User;
import com.idea_forge.modules.user.repository.EmailVerificationTokenRepository;
import com.idea_forge.modules.user.repository.UserRepository;

@Service
public class EmailVerificationService {

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    private final UserRepository userRepository;

    public EmailVerificationService(EmailVerificationTokenRepository emailVerificationTokenRepository,
            UserRepository userRepository) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EmailVerificationToken createVerificationToken(User user) {
        String tokenValue = UUID.randomUUID().toString();

        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(24));
        token.setUsedAt(null);

        return emailVerificationTokenRepository.save(token);
    }

    public void sendVerificationEmail(User user, String token) {

        String verificationLink = "http://localhost:3000/verify-email?token=" + token;

        System.out.println(
                "[EMAIL_SIMULADO] " +
                        user.getEmail() +
                        " -> " +
                        verificationLink);
    }

    @Transactional
    public EmailVerificationResponseDTO verifyEmail(String tokenValue) {
        EmailVerificationToken token = emailVerificationTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new InvalidVerificationTokenException("Token inválido."));

        User user = token.getUser();

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new EmailAlreadyVerifiedException("E-mail já foi validado.");
        }

        if (token.getUsedAt() != null) {
            throw new VerificationTokenAlreadyUsedException("Token já utilizado.");
        }

        LocalDateTime now = LocalDateTime.now();

        if (token.getExpiresAt().isBefore(now)) {
            throw new ExpiredVerificationTokenException("Token expirado.");
        }

        user.setEmailVerified(true);
        token.setUsedAt(now);

        userRepository.save(user);
        emailVerificationTokenRepository.save(token);

        return new EmailVerificationResponseDTO("E-mail validado com sucesso.");
    }

    public EmailVerificationResponseDTO verifyEmail(VerifyEmailRequestDTO request) {
        return verifyEmail(request.getToken());
    }
}
