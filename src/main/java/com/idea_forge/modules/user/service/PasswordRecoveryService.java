package com.idea_forge.modules.user.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idea_forge.common.exception.InvalidPasswordResetTokenException;
import com.idea_forge.modules.user.dto.ForgotPasswordResponseDTO;
import com.idea_forge.modules.user.dto.MessageResponseDTO;
import com.idea_forge.modules.user.dto.ResetPasswordRequestDTO;
import com.idea_forge.modules.user.dto.ValidateResetTokenResponseDTO;
import com.idea_forge.modules.user.entity.PasswordResetToken;
import com.idea_forge.modules.user.entity.User;
import com.idea_forge.modules.user.repository.PasswordResetTokenRepository;
import com.idea_forge.modules.user.repository.UserRepository;

@Service
public class PasswordRecoveryService {

    private static final int TOKEN_EXPIRATION_MINUTES = 30;
    private static final String FORGOT_PASSWORD_MESSAGE = "Se o e-mail estiver cadastrado, as instruções para recuperação foram enviadas.";
    private static final String INVALID_TOKEN_MESSAGE = "Link de recuperação inválido ou expirado.";
    private static final String PASSWORD_CHANGED_MESSAGE = "Senha alterada com sucesso.";

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordRecoveryService(UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ForgotPasswordResponseDTO forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            return new ForgotPasswordResponseDTO(FORGOT_PASSWORD_MESSAGE);
        }

        User user = userOptional.get();
        LocalDateTime now = LocalDateTime.now();
        invalidateActiveTokens(user, now);

        String tokenValue = UUID.randomUUID().toString();

        PasswordResetToken token = new PasswordResetToken();
        token.setToken(tokenValue);
        token.setUser(user);
        token.setExpiresAt(now.plusMinutes(TOKEN_EXPIRATION_MINUTES));
        token.setUsedAt(null);
        passwordResetTokenRepository.save(token);

        return new ForgotPasswordResponseDTO(FORGOT_PASSWORD_MESSAGE, tokenValue);
    }

    public ValidateResetTokenResponseDTO validateToken(String token) {
        if (findValidToken(token).isPresent()) {
            return new ValidateResetTokenResponseDTO(true);
        }

        return new ValidateResetTokenResponseDTO(false, INVALID_TOKEN_MESSAGE);
    }

    @Transactional
    public MessageResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) {
        PasswordResetToken resetToken = findValidToken(resetPasswordRequestDTO.getToken())
                .orElseThrow(() -> new InvalidPasswordResetTokenException(INVALID_TOKEN_MESSAGE));

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequestDTO.getPassword()));
        userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        resetToken.setUsedAt(now);
        passwordResetTokenRepository.save(resetToken);

        return new MessageResponseDTO(PASSWORD_CHANGED_MESSAGE);
    }

    private void invalidateActiveTokens(User user, LocalDateTime now) {
        passwordResetTokenRepository.findByUserAndUsedAtIsNull(user).forEach(activeToken -> {
            activeToken.setUsedAt(now);
            passwordResetTokenRepository.save(activeToken);
        });
    }

    private Optional<PasswordResetToken> findValidToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        return passwordResetTokenRepository.findByTokenAndUsedAtIsNullAndExpiresAtAfter(token, LocalDateTime.now());
    }

}
