package com.idea_forge.common.config;

import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.idea_forge.modules.user.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "accessToken";
    private static final String LEGACY_ACCESS_TOKEN_COOKIE_NAME = "access_token";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }

        String path = request.getServletPath();
        return "/auth/resend-verification-email".equals(path)
                || "/auth/verify-email".equals(path)
                || "/users".equals(path)
                || "/users/login".equals(path)
                || "/users/refresh".equals(path)
                || "/users/logout".equals(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        SecurityContextHolder.clearContext();

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if (ACCESS_TOKEN_COOKIE_NAME.equals(cookieName) || LEGACY_ACCESS_TOKEN_COOKIE_NAME.equals(cookieName)) {
                    String token = cookie.getValue();
                    if (jwtService.isTokenValid(token)) {
                        String email = jwtService.extractEmail(token);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                Collections.emptyList());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                    break;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
