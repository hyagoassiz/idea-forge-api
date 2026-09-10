package com.idea_forge.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.idea_forge.modules.user.dto.ForgotPasswordRequestDTO;
import com.idea_forge.modules.user.dto.ForgotPasswordResponseDTO;
import com.idea_forge.modules.user.dto.MessageResponseDTO;
import com.idea_forge.modules.user.dto.ResetPasswordRequestDTO;
import com.idea_forge.modules.user.dto.ValidateResetTokenResponseDTO;
import com.idea_forge.modules.user.service.PasswordRecoveryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final PasswordRecoveryService passwordRecoveryService;

    public AuthController(PasswordRecoveryService passwordRecoveryService) {
        this.passwordRecoveryService = passwordRecoveryService;
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
    public ResponseEntity<MessageResponseDTO> resetPassword(
            @Valid @RequestBody ResetPasswordRequestDTO request) {
        return ResponseEntity.ok(passwordRecoveryService.resetPassword(request));
    }

}
