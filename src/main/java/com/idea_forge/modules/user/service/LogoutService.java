package com.idea_forge.modules.user.service;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class LogoutService {

    private static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    private static final String COOKIE_PATH = "/";

    /**
     * Logout by removing the access token cookie
     * 
     * @param response HTTP response to add the expired cookie
     */
    public void logout(HttpServletResponse response) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath(COOKIE_PATH);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
