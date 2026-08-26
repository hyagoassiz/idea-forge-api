package com.idea_forge.modules.user.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idea_forge.modules.user.dto.LoginRequestDTO;
import com.idea_forge.modules.user.dto.LoginResponseDTO;
import com.idea_forge.modules.user.dto.TokenResponseDTO;
import com.idea_forge.modules.user.dto.UserRequestDTO;
import com.idea_forge.modules.user.dto.UserResponseDTO;
import com.idea_forge.modules.user.service.JwtService;
import com.idea_forge.modules.user.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("users")
public class UserController {

        private final UserService userService;
        private final JwtService jwtService;

        public UserController(UserService userService, JwtService jwtService) {
                this.userService = userService;
                this.jwtService = jwtService;
        }

        @PostMapping
        public UserResponseDTO createUser(
                        @Valid @RequestBody UserRequestDTO userRequestDTO) {
                return userService.createUser(userRequestDTO);
        }

        @PostMapping("/login")
        public ResponseEntity<LoginResponseDTO> login(
                        @RequestBody LoginRequestDTO loginRequestDTO) {
                TokenResponseDTO tokens = userService.login(loginRequestDTO);

                LoginResponseDTO response = new LoginResponseDTO(
                                "Login realizado com sucesso",
                                loginRequestDTO.getEmail());

                return ResponseEntity.ok()
                                .header(
                                                HttpHeaders.SET_COOKIE,
                                                jwtService.generateAccessTokenCookie(tokens.getAccessToken()))
                                .header(
                                                HttpHeaders.SET_COOKIE,
                                                jwtService.generateRefreshTokenCookie(tokens.getRefreshToken()))
                                .body(response);
        }

        @GetMapping("/me")
        public ResponseEntity<UserResponseDTO> getAuthenticatedUser() {
                UserResponseDTO response = userService.getAuthenticatedUser();
                return ResponseEntity.ok(response);
        }

        @PostMapping("/refresh")
        public ResponseEntity<LoginResponseDTO> refresh(
                        @CookieValue(name = "refreshToken", required = false) String refreshToken) {

                if (refreshToken == null || refreshToken.isEmpty()) {
                        throw new RuntimeException("Refresh token não encontrado");
                }

                TokenResponseDTO tokens = userService.refresh(refreshToken);

                LoginResponseDTO response = new LoginResponseDTO(
                                "Token renovado com sucesso",
                                null);

                return ResponseEntity.ok()
                                .header(
                                                HttpHeaders.SET_COOKIE,
                                                jwtService.generateAccessTokenCookie(tokens.getAccessToken()))
                                .header(
                                                HttpHeaders.SET_COOKIE,
                                                jwtService.generateRefreshTokenCookie(tokens.getRefreshToken()))
                                .body(response);
        }
}
