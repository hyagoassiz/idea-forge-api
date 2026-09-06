package com.idea_forge.modules.user.service;

import com.idea_forge.modules.user.entity.User;

public interface EmailVerificationEmailSender {
    void sendVerificationEmail(User user, String token);
}
