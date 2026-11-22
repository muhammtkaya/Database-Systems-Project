package com.etu.expensemanagement.security;

import com.etu.expensemanagement.exception.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * Reads the caller's employee id from a request attribute
 * puts the id -> nothing did -> unidentified -> 401.
 */
@Component
public class RequestAttributeCurrentUser implements CurrentUser {

    public static final String ATTRIBUTE = "currentEmployeeId";

    private final HttpServletRequest request;

    public RequestAttributeCurrentUser(HttpServletRequest request) {
        this.request = request; // Spring -> proxy -> current request
    }

    @Override
    public Long employeeId() {
        Object value = request.getAttribute(ATTRIBUTE);
        if (value instanceof Long id) {
            return id;
        }
        throw new UnauthorizedException("No identified employee for this request");
    }
}
