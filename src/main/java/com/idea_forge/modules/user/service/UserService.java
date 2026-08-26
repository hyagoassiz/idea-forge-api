package com.idea_forge.modules.user.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.idea_forge.common.exception.EmailAlreadyExistsException;
import com.idea_forge.common.exception.InvalidCredentialsException;
import com.idea_forge.modules.user.dto.LoginRequestDTO;
import com.idea_forge.modules.user.dto.TokenResponseDTO;
import com.idea_forge.modules.user.dto.UserRequestDTO;
import com.idea_forge.modules.user.dto.UserResponseDTO;
import com.idea_forge.modules.user.entity.User;
import com.idea_forge.modules.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    public UserService(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }

        User user = User.builder()
                .name(userRequestDTO.getName().trim().replaceAll("\\s+", " "))
                .email(userRequestDTO.getEmail())
                .password(passwordEncoder.encode(userRequestDTO.getPassword()))
                .build();

        User savedUser = userRepository.save(user);

        return new UserResponseDTO(savedUser.getName(), savedUser.getEmail());
    }

    public TokenResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas"));

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new TokenResponseDTO(accessToken, refreshToken);
    }

    public UserResponseDTO getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal() == null
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas"));

        return new UserResponseDTO(user.getName(), user.getEmail());
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
