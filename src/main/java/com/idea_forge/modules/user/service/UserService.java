package com.idea_forge.modules.user.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idea_forge.common.exception.EmailAlreadyExistsException;
import com.idea_forge.common.exception.InvalidCredentialsException;
import com.idea_forge.modules.user.dto.CreateUserRequestDTO;
import com.idea_forge.modules.user.dto.CreateUserResponseDTO;
import com.idea_forge.modules.user.dto.LoginRequestDTO;
import com.idea_forge.modules.user.dto.TokenResponseDTO;
import com.idea_forge.modules.user.entity.EmailVerificationToken;
import com.idea_forge.modules.user.entity.User;
import com.idea_forge.modules.user.mapper.UserMapper;
import com.idea_forge.modules.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final UserMapper userMapper;

    private final EmailVerificationService emailVerificationService;

    public UserService(UserRepository userRepository,
            JwtService jwtService,
            UserMapper userMapper,
            EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.emailVerificationService = emailVerificationService;
    }

    @Transactional
    public CreateUserResponseDTO createUser(CreateUserRequestDTO createUserRequestDTO) {

        if (userRepository.findByEmail(createUserRequestDTO.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email já cadastrado");
        }

        User user = userMapper.toEntity(createUserRequestDTO);
        user.setPassword(passwordEncoder.encode(createUserRequestDTO.getPassword()));
        user.setEmailVerified(false);

        User savedUser = userRepository.save(user);

        EmailVerificationToken emailVerificationToken = emailVerificationService.createVerificationToken(savedUser);
        emailVerificationService.sendVerificationEmail(savedUser, emailVerificationToken.getToken());

        return userMapper.toCreateResponse(savedUser);
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

    // public UserResponseDTO getAuthenticatedUser() {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();

    // if (authentication == null || !authentication.isAuthenticated()
    // || authentication.getPrincipal() == null
    // || "anonymousUser".equals(authentication.getPrincipal())) {
    // throw new InvalidCredentialsException("Credenciais inválidas");
    // }

    // String email = authentication.getName();
    // User user = userRepository.findByEmail(email)
    // .orElseThrow(() -> new InvalidCredentialsException("Credenciais inválidas"));

    // return userMapper.toUserResponse(user);
    // }

    // public TokenResponseDTO refresh(String refreshToken) {
    // if (!jwtService.isTokenValid(refreshToken)) {
    // throw new RuntimeException("Refresh token inválido");
    // }

    // if (!jwtService.extractTokenType(refreshToken).equals("refresh")) {
    // throw new RuntimeException("Token não é um refresh token");
    // }

    // String email = jwtService.extractEmail(refreshToken);

    // User user = userRepository.findByEmail(email)
    // .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

    // String newAccessToken = jwtService.generateAccessToken(user);
    // String newRefreshToken = jwtService.generateRefreshToken(user);

    // return new TokenResponseDTO(newAccessToken, newRefreshToken);
    // }
}
