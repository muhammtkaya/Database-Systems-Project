package com.etu.expensemanagement.web;

import com.etu.expensemanagement.config.WebConfig;
import com.etu.expensemanagement.controller.DevController;
import com.etu.expensemanagement.security.RequestAttributeCurrentUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DevController.class)
@Import({WebConfig.class, RequestAttributeCurrentUser.class})
class DevIdentityAndErrorHandlingTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void withoutIdentityHeader_returns401InStandardErrorShape() throws Exception {
        mockMvc.perform(get("/api/dev/whoami"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/dev/whoami"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void withValidIdentityHeader_returnsThatEmployee() throws Exception {
        mockMvc.perform(get("/api/dev/whoami").header("X-Dev-Employee-Id", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value(7));
    }

    @Test
    void nonNumericIdentityHeader_returns401() throws Exception {
        mockMvc.perform(get("/api/dev/whoami").header("X-Dev-Employee-Id", "abc"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void zeroOrNegativeIdentityHeader_returns401() throws Exception {
        mockMvc.perform(get("/api/dev/whoami").header("X-Dev-Employee-Id", "-3"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unknownUrl_returns404InStandardErrorShape() throws Exception {
        mockMvc.perform(get("/api/does-not-exist").header("X-Dev-Employee-Id", "7"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void wrongHttpMethod_returns405() throws Exception {
        mockMvc.perform(post("/api/dev/whoami").header("X-Dev-Employee-Id", "7"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405));
    }

    @Test
    void corsPreflightFromAllowedOrigin_isAcceptedWithoutIdentityHeader() throws Exception {
        mockMvc.perform(options("/api/dev/whoami")
                        .header("Origin", "http://localhost:5173")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "X-Dev-Employee-Id"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:5173"));
    }
}
