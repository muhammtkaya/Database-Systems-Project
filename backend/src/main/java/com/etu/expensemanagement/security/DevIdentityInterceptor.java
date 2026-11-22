package com.etu.expensemanagement.security;

import com.etu.expensemanagement.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class DevIdentityInterceptor implements HandlerInterceptor {

    public static final String HEADER = "X-Dev-Employee-Id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true; 
        }
        String raw = request.getHeader(HEADER);
        if (raw == null || raw.isBlank()) {
            throw new UnauthorizedException("Missing header " + HEADER + " (temporary development identity)");
        }
        long id;
        try {
            id = Long.parseLong(raw.trim());
        } catch (NumberFormatException e) {
            throw new UnauthorizedException("Header " + HEADER + " must be a positive number");
        }
        if (id <= 0) {
            throw new UnauthorizedException("Header " + HEADER + " must be a positive number");
        }
        request.setAttribute(RequestAttributeCurrentUser.ATTRIBUTE, id);
        return true;
    }
}
