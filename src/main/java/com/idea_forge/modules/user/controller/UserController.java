package com.idea_forge.modules.user.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idea_forge.modules.user.dto.CreateUserRequestDTO;
import com.idea_forge.modules.user.dto.CreateUserResponseDTO;
import com.idea_forge.modules.user.dto.LoginRequestDTO;
import com.idea_forge.modules.user.dto.LoginResponseDTO;
import com.idea_forge.modules.user.dto.TokenResponseDTO;
import com.idea_forge.modules.user.service.JwtService;
import com.idea_forge.modules.user.service.LogoutService;
import com.idea_forge.modules.user.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("users")
public class UserController {

        private final UserService userService;

        private final JwtService jwtService;

        private final LogoutService logoutService;

        public UserController(UserService userService, JwtService jwtService, LogoutService logoutService) {
                this.userService = userService;
                this.jwtService = jwtService;
                this.logoutService = logoutService;
        }

        @PostMapping
        public CreateUserResponseDTO createUser(
                        @Valid @RequestBody CreateUserRequestDTO createUserRequestDTO) {
                return userService.createUser(createUserRequestDTO);
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

        @PostMapping("/logout")
        public ResponseEntity<Void> logout(HttpServletResponse response) {
                logoutService.logout(response);
                return ResponseEntity.noContent().build();
        }

        // @GetMapping("/me")
        // public ResponseEntity<UserResponseDTO> getAuthenticatedUser() {
        // UserResponseDTO response = userService.getAuthenticatedUser();
        // return ResponseEntity.ok(response);
        // }

        // @PostMapping("/refresh")
        // public ResponseEntity<LoginResponseDTO> refresh(
        // @CookieValue(name = "refreshToken", required = false) String refreshToken) {

        // if (refreshToken == null || refreshToken.isEmpty()) {
        // throw new RuntimeException("Refresh token não encontrado");
        // }

        // TokenResponseDTO tokens = userService.refresh(refreshToken);

        // LoginResponseDTO response = new LoginResponseDTO(
        // "Token renovado com sucesso",
        // null);

        // return ResponseEntity.ok()
        // .header(
        // HttpHeaders.SET_COOKIE,
        // jwtService.generateAccessTokenCookie(tokens.getAccessToken()))
        // .header(
        // HttpHeaders.SET_COOKIE,
        // jwtService.generateRefreshTokenCookie(tokens.getRefreshToken()))
        // .body(response);
        // }
}
