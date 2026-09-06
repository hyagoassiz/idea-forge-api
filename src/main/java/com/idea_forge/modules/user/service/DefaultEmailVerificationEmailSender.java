package com.idea_forge.modules.user.service;

import org.springframework.stereotype.Service;

import com.idea_forge.modules.user.entity.User;

@Service
public class DefaultEmailVerificationEmailSender implements EmailVerificationEmailSender {

    @Override
    public void sendVerificationEmail(User user, String token) {
        String verificationLink = "http://localhost:3000/verify-email?token=" + token;

        System.out.println(
                "[EMAIL_SIMULADO] " +
                        user.getEmail() +
                        " -> " +
                        verificationLink);
    }
}
