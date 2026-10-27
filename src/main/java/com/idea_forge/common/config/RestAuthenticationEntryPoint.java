package com.idea_forge.common.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");

        String payload = "{\"status\":401,\"code\":\"AUTH_TOKEN_EXPIRED\",\"message\":\"Sessão expirada ou inválida.\",\"errors\":[{\"field\":\"general\",\"code\":\"AUTH_TOKEN_EXPIRED\",\"message\":\"Sessão expirada ou inválida.\"}]}";
        response.getWriter().write(payload);
    }
}
