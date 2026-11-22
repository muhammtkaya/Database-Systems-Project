package com.etu.expensemanagement.db;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** HTTP -> controller -> service -> repository -> PostgreSQL. */
@SpringBootTest
@AutoConfigureMockMvc
class ApiDatabaseIT {

    @Autowired
    MockMvc mockMvc;

    @Test
    void vendors_returnsRowsFromDatabase() throws Exception {
        mockMvc.perform(get("/api/vendors").header("X-Dev-Employee-Id", "109"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(20)))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").exists());
    }

    @Test
    void vendors_withoutIdentity_returns401() throws Exception {
        mockMvc.perform(get("/api/vendors")).andExpect(status().isUnauthorized());
    }

    @Test
    void me_forStaffEmployee() throws Exception {
        mockMvc.perform(get("/api/employees/me").header("X-Dev-Employee-Id", "109"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(109))
                .andExpect(jsonPath("$.firstName").value("Emre"))
                .andExpect(jsonPath("$.lastName").value("Arslan"))
                .andExpect(jsonPath("$.departmentNo").value(5))
                .andExpect(jsonPath("$.supervisorId").value(105))
                .andExpect(jsonPath("$.manager").value(false));
    }

    @Test
    void me_forManager() throws Exception {
        mockMvc.perform(get("/api/employees/me").header("X-Dev-Employee-Id", "105"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supervisorId").value(102))
                .andExpect(jsonPath("$.manager").value(true));
    }

    @Test
    void me_forTopOfHierarchy_hasNoSupervisor() throws Exception {
        mockMvc.perform(get("/api/employees/me").header("X-Dev-Employee-Id", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supervisorId").value(nullValue()));
    }

    @Test
    void me_forUnknownEmployee_returns401() throws Exception {
        mockMvc.perform(get("/api/employees/me").header("X-Dev-Employee-Id", "999"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unknown employee"));
    }

    @Test
    void me_forHugeId_returns401() throws Exception {
        mockMvc.perform(get("/api/employees/me").header("X-Dev-Employee-Id", "99999999999"))
                .andExpect(status().isUnauthorized());
    }
}
