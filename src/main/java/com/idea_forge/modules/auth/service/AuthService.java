package com.idea_forge.modules.auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.idea_forge.common.exception.InvalidCredentialsException;
import com.idea_forge.common.service.JwtService;
import com.idea_forge.modules.auth.dto.LoginRequestDTO;
import com.idea_forge.modules.auth.dto.TokenResponseDTO;
import com.idea_forge.modules.user.entity.User;
import com.idea_forge.modules.user.repository.UserRepository;

@Service
public class AuthService {

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
            JwtService jwtService) {
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(),
                user.getPassword())) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }

        if (Boolean.FALSE.equals(user.getEmailVerified())) {
            throw new com.idea_forge.common.exception.EmailNotVerifiedException(
                    "E-mail ainda não foi validado. Verifique sua caixa de entrada ou solicite um novo e-mail de validação.");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    public TokenResponseDTO refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Refresh token inválido");
        }

        if (!jwtService.extractTokenType(refreshToken).equals("refresh")) {
            throw new RuntimeException("Token não é um refresh token");
        }

        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new TokenResponseDTO(newAccessToken, newRefreshToken);
    }

}
