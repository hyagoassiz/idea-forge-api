package com.idea_forge.modules.user.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.idea_forge.common.exception.EmailAlreadyExistsException;
import com.idea_forge.common.exception.InvalidCredentialsException;
import com.idea_forge.modules.auth.entity.EmailVerificationToken;
import com.idea_forge.modules.auth.service.EmailVerificationService;
import com.idea_forge.modules.user.dto.CreateUserRequestDTO;
import com.idea_forge.modules.user.dto.CreateUserResponseDTO;
import com.idea_forge.modules.user.dto.UserResponseDTO;
import com.idea_forge.modules.user.entity.User;
import com.idea_forge.modules.user.mapper.UserMapper;
import com.idea_forge.modules.user.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final EmailVerificationService emailVerificationService;

    public UserService(UserRepository userRepository,
            UserMapper userMapper,
            EmailVerificationService emailVerificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
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

        // TODO: Remove this temporary token exposure once the email verification flow
        // is final.
        CreateUserResponseDTO response = userMapper.toCreateResponse(savedUser);
        response.setToken(emailVerificationToken.getToken());
        return response;
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

        return userMapper.toUserResponse(user);
    }

}
