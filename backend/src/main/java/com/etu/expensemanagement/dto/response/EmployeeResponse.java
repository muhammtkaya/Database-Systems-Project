package com.etu.expensemanagement.dto.response;

import java.time.LocalDate;

/** What the API shows about an employee (frontend needs) */
public record EmployeeResponse(
        Integer id,
        String firstName,
        String lastName,
        Integer departmentNo,
        String departmentName,
        Integer supervisorId,
        LocalDate startDate,
        boolean manager) {
}
