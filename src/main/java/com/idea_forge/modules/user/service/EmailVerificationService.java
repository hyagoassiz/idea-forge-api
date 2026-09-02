package com.idea_forge.modules.user.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idea_forge.common.exception.EmailAlreadyVerifiedException;
import com.idea_forge.common.exception.EmailVerificationSendFailedException;
import com.idea_forge.common.exception.ExpiredVerificationTokenException;
import com.idea_forge.common.exception.InvalidVerificationTokenException;
import com.idea_forge.common.exception.TooManyVerificationRequestsException;
import com.idea_forge.common.exception.VerificationTokenAlreadyUsedException;
import com.idea_forge.modules.user.dto.EmailVerificationResponseDTO;
import com.idea_forge.modules.user.dto.VerifyEmailRequestDTO;
import com.idea_forge.modules.user.entity.EmailVerificationToken;
import com.idea_forge.modules.user.entity.User;
import com.idea_forge.modules.user.repository.EmailVerificationTokenRepository;
import com.idea_forge.modules.user.repository.UserRepository;

@Service
public class EmailVerificationService {

    private static final int RESEND_COOLDOWN_MINUTES = 5;

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;

    private final UserRepository userRepository;

    private final EmailVerificationEmailSender emailVerificationEmailSender;

    public EmailVerificationService(EmailVerificationTokenRepository emailVerificationTokenRepository,
            UserRepository userRepository,
            EmailVerificationEmailSender emailVerificationEmailSender) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
        this.emailVerificationEmailSender = emailVerificationEmailSender;
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
        emailVerificationEmailSender.sendVerificationEmail(user, token);
    }

    @Transactional
    public EmailVerificationResponseDTO resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidVerificationTokenException("Usuário não encontrado."));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new EmailAlreadyVerifiedException("E-mail já foi validado.");
        }

        LocalDateTime now = LocalDateTime.now();

        // Check existing token for this user
        java.util.Optional<EmailVerificationToken> existingTokenOpt = emailVerificationTokenRepository.findByUser(user);
        if (existingTokenOpt.isPresent()) {
            EmailVerificationToken existing = existingTokenOpt.get();
            if (existing.getCreatedAt() != null
                    && existing.getCreatedAt().isAfter(now.minusMinutes(RESEND_COOLDOWN_MINUTES))) {
                throw new TooManyVerificationRequestsException(
                        "Aguarde 5 minutos antes de solicitar um novo e-mail de validação.");
            }

            // remove previous token
            emailVerificationTokenRepository.delete(existing);
        }

        EmailVerificationToken newToken = createVerificationToken(user);

        try {
            sendVerificationEmail(user, newToken.getToken());
        } catch (RuntimeException ex) {
            throw new EmailVerificationSendFailedException("Não foi possível enviar o e-mail de validação.");
        }

        // TODO: Remove this temporary token exposure once the email verification flow
        // is final.
        return new EmailVerificationResponseDTO("novo e-mail de validação enviado com sucesso.", newToken.getToken());
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
