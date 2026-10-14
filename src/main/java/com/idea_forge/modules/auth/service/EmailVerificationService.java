package com.idea_forge.modules.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idea_forge.common.exception.EmailAlreadyVerifiedException;
import com.idea_forge.common.exception.EmailSendFailedException;
import com.idea_forge.common.exception.EmailVerificationSendFailedException;
import com.idea_forge.common.exception.ExpiredVerificationTokenException;
import com.idea_forge.common.exception.InvalidVerificationTokenException;
import com.idea_forge.common.exception.TooManyVerificationRequestsException;
import com.idea_forge.common.exception.VerificationTokenAlreadyUsedException;
import com.idea_forge.modules.auth.dto.EmailVerificationResponseDTO;
import com.idea_forge.modules.auth.dto.VerifyEmailRequestDTO;
import com.idea_forge.modules.auth.entity.EmailVerificationToken;
import com.idea_forge.modules.auth.repository.EmailVerificationTokenRepository;
import com.idea_forge.modules.user.entity.User;
import com.idea_forge.modules.user.repository.UserRepository;

@Service
public class EmailVerificationService {

    private static final int RESEND_COOLDOWN_MINUTES = 5;

    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final UserRepository userRepository;
    private final AuthEmailSender authEmailSender;

    public EmailVerificationService(
            EmailVerificationTokenRepository emailVerificationTokenRepository,
            UserRepository userRepository,
            AuthEmailSender authEmailSender) {
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.userRepository = userRepository;
        this.authEmailSender = authEmailSender;
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
        try {
            authEmailSender.sendVerificationEmail(user, token);
        } catch (EmailSendFailedException ex) {
            throw new EmailVerificationSendFailedException("Não foi possível enviar o e-mail de validação.", ex);
        }
    }

    @Transactional
    public EmailVerificationResponseDTO resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidVerificationTokenException("Usuário não encontrado."));

        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new EmailAlreadyVerifiedException("E-mail já foi validado.");
        }

        LocalDateTime now = LocalDateTime.now();

        Optional<EmailVerificationToken> existingTokenOpt = emailVerificationTokenRepository.findByUser(user);
        if (existingTokenOpt.isPresent()) {
            EmailVerificationToken existing = existingTokenOpt.get();
            if (existing.getCreatedAt() != null
                    && existing.getCreatedAt().isAfter(now.minusMinutes(RESEND_COOLDOWN_MINUTES))) {
                throw new TooManyVerificationRequestsException(
                        "Aguarde 5 minutos antes de solicitar um novo e-mail de validação.");
            }

            emailVerificationTokenRepository.delete(existing);
            emailVerificationTokenRepository.flush();
        }

        EmailVerificationToken newToken = createVerificationToken(user);
        sendVerificationEmail(user, newToken.getToken());

        return new EmailVerificationResponseDTO("Novo e-mail de validação enviado com sucesso.");
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
