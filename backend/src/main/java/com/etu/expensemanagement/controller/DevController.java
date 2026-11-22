package com.etu.expensemanagement.controller;

import com.etu.expensemanagement.security.CurrentUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** TEMPORARY helper to test */
@RestController
@RequestMapping("/api/dev")
@ConditionalOnProperty(name = "app.dev-identity.enabled", havingValue = "true")
public class DevController {

    private final CurrentUser currentUser;

    public DevController(CurrentUser currentUser) {
        this.currentUser = currentUser;
    }

    @GetMapping("/whoami")
    public WhoAmIResponse whoAmI() {
        return new WhoAmIResponse(currentUser.employeeId());
    }

    public record WhoAmIResponse(Long employeeId) {
    }
}
