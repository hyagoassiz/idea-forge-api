package com.idea_forge.common.config;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String payload = "{\"status\":403,\"code\":\"ACCESS_DENIED\",\"message\":\"Você não tem permissão para acessar este recurso.\",\"errors\":[{\"field\":\"general\",\"code\":\"ACCESS_DENIED\",\"message\":\"Você não tem permissão para acessar este recurso.\"}]}";
        response.getWriter().write(payload);
    }
}
