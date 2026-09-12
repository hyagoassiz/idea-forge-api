package com.idea_forge.modules.auth.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.idea_forge.common.service.JwtService;
import com.idea_forge.modules.auth.dto.EmailVerificationResponseDTO;
import com.idea_forge.modules.auth.dto.ForgotPasswordRequestDTO;
import com.idea_forge.modules.auth.dto.ForgotPasswordResponseDTO;
import com.idea_forge.modules.auth.dto.LoginRequestDTO;
import com.idea_forge.modules.auth.dto.LoginResponseDTO;
import com.idea_forge.modules.auth.dto.ResendVerificationEmailRequestDTO;
import com.idea_forge.modules.auth.dto.ResetPasswordRequestDTO;
import com.idea_forge.modules.auth.dto.ResetPasswordResponseDTO;
import com.idea_forge.modules.auth.dto.TokenResponseDTO;
import com.idea_forge.modules.auth.dto.ValidateResetTokenResponseDTO;
import com.idea_forge.modules.auth.dto.VerifyEmailRequestDTO;
import com.idea_forge.modules.auth.service.AuthService;
import com.idea_forge.modules.auth.service.EmailVerificationService;
import com.idea_forge.modules.auth.service.LogoutService;
import com.idea_forge.modules.auth.service.PasswordRecoveryService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

        private final AuthService authService;

        private final PasswordRecoveryService passwordRecoveryService;

        private final JwtService jwtService;

        private final LogoutService logoutService;

        private final EmailVerificationService emailVerificationService;

        public AuthController(AuthService authService, PasswordRecoveryService passwordRecoveryService,
                        JwtService jwtService, LogoutService logoutService,
                        EmailVerificationService emailVerificationService) {
                this.authService = authService;
                this.passwordRecoveryService = passwordRecoveryService;
                this.jwtService = jwtService;
                this.logoutService = logoutService;
                this.emailVerificationService = emailVerificationService;
        }

        @PostMapping("/login")
        public ResponseEntity<LoginResponseDTO> login(
                        @RequestBody LoginRequestDTO loginRequestDTO) {
                TokenResponseDTO tokens = authService.login(loginRequestDTO);

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

        @PostMapping("/forgot-password")
        public ResponseEntity<ForgotPasswordResponseDTO> forgotPassword(
                        @Valid @RequestBody ForgotPasswordRequestDTO forgotPasswordRequestDTO) {
                return ResponseEntity.ok(passwordRecoveryService.forgotPassword(forgotPasswordRequestDTO.getEmail()));
        }

        @GetMapping("/reset-password/validate")
        public ResponseEntity<ValidateResetTokenResponseDTO> validateResetToken(@RequestParam String token) {
                ValidateResetTokenResponseDTO response = passwordRecoveryService.validateToken(token);
                if (!response.isValid()) {
                        return ResponseEntity.badRequest().body(response);
                }
                return ResponseEntity.ok(response);
        }

        @PostMapping("/reset-password")
        public ResponseEntity<ResetPasswordResponseDTO> resetPassword(
                        @Valid @RequestBody ResetPasswordRequestDTO request) {
                return ResponseEntity.ok(passwordRecoveryService.resetPassword(request));
        }

        @PostMapping("/refresh")
        public ResponseEntity<LoginResponseDTO> refresh(
                        @CookieValue(name = "refreshToken", required = false) String refreshToken) {

                if (refreshToken == null || refreshToken.isEmpty()) {
                        throw new RuntimeException("Refresh token não encontrado");
                }

                TokenResponseDTO tokens = authService.refresh(refreshToken);
                String email = jwtService.extractEmail(refreshToken);

                LoginResponseDTO response = new LoginResponseDTO(
                                "Token renovado com sucesso",
                                email);

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

        @PostMapping("/verify-email")
        public ResponseEntity<EmailVerificationResponseDTO> verifyEmail(
                        @Valid @RequestBody VerifyEmailRequestDTO verifyEmailRequestDTO) {
                EmailVerificationResponseDTO response = emailVerificationService.verifyEmail(verifyEmailRequestDTO);
                return ResponseEntity.ok(response);
        }

        @PostMapping("/resend-verification-email")
        public ResponseEntity<EmailVerificationResponseDTO> resendVerificationEmail(
                        @Valid @RequestBody ResendVerificationEmailRequestDTO resendVerificationEmailRequestDTO) {
                EmailVerificationResponseDTO response = emailVerificationService
                                .resendVerificationEmail(resendVerificationEmailRequestDTO.getEmail());
                return ResponseEntity.ok(response);
        }

}
