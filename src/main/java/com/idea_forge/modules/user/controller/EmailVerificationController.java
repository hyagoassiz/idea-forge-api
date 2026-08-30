package com.idea_forge.modules.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.idea_forge.modules.user.dto.EmailVerificationResponseDTO;
import com.idea_forge.modules.user.dto.ResendVerificationEmailRequestDTO;
import com.idea_forge.modules.user.dto.VerifyEmailRequestDTO;
import com.idea_forge.modules.user.service.EmailVerificationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class EmailVerificationController {

    private final EmailVerificationService emailVerificationService;

    public EmailVerificationController(EmailVerificationService emailVerificationService) {
        this.emailVerificationService = emailVerificationService;
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
