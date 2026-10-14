package com.idea_forge.modules.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.idea_forge.common.service.EmailService;
import com.idea_forge.modules.user.entity.User;

@Service
public class AuthEmailSender {

    private final EmailService emailService;
    private final String frontendUrl;
    private final String verificationPath;
    private final String resetPasswordPath;

    public AuthEmailSender(
            EmailService emailService,
            @Value("${app.frontend.url}") String frontendUrl,
            @Value("${app.frontend.verify-email.path}") String verificationPath,
            @Value("${app.frontend.reset-password.path}") String resetPasswordPath) {
        this.emailService = emailService;
        this.frontendUrl = frontendUrl;
        this.verificationPath = verificationPath;
        this.resetPasswordPath = resetPasswordPath;
    }

    public void sendVerificationEmail(User user, String token) {
        String verificationLink = buildFrontendLink(verificationPath, token);
        emailService.sendHtmlEmail(
                user.getEmail(),
                "Confirmação de e-mail - IdeaForge",
                buildVerificationHtml(user.getName(), verificationLink));
    }

    public void sendPasswordResetEmail(User user, String token) {
        String resetUrl = buildFrontendLink(resetPasswordPath, token);
        emailService.sendHtmlEmail(
                user.getEmail(),
                "Recuperação de senha - IdeaForge",
                buildPasswordResetHtml(resetUrl));
    }

    private String buildFrontendLink(String path, String token) {
        String base = frontendUrl.endsWith("/") ? frontendUrl.substring(0, frontendUrl.length() - 1) : frontendUrl;
        String normalizedPath = path.startsWith("/") ? path : "/" + path;
        return base + normalizedPath + "?token=" + token;
    }

    private String buildVerificationHtml(String name, String verificationLink) {
        return "<html><body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #1f2937;\">"
                + "<h2>Confirmação de e-mail</h2>"
                + "<p>Olá, " + escapeHtml(name) + "!</p>"
                + "<p>Para concluir seu cadastro, clique no botão abaixo:</p>"
                + "<p><a href=\"" + verificationLink
                + "\" style=\"display: inline-block; background-color: #16a34a; color: #ffffff; text-decoration: none; padding: 12px 20px; border-radius: 8px;\">Confirmar e-mail</a></p>"
                + "<p>Se você não criou uma conta, ignore este e-mail.</p>"
                + "</body></html>";
    }

    private String buildPasswordResetHtml(String resetUrl) {
        return "<html><body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #1f2937;\">"
                + "<h2>Recuperação de senha</h2>"
                + "<p>Recebemos uma solicitação para redefinir sua senha.</p>"
                + "<p>Clique no botão abaixo para continuar:</p>"
                + "<p><a href=\"" + resetUrl
                + "\" style=\"display: inline-block; background-color: #2563eb; color: #ffffff; text-decoration: none; padding: 12px 20px; border-radius: 8px;\">Redefinir senha</a></p>"
                + "<p>Se você não solicitou esta redefinição, ignore este e-mail.</p>"
                + "<p>Este link expira em 30 minutos.</p>"
                + "</body></html>";
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
