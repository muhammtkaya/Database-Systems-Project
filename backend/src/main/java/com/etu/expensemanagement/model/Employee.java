package com.etu.expensemanagement.model;

import java.time.LocalDate;

/** One row of expense_management.employee. */
public record Employee(
        Integer employeeId,
        String firstName,
        String lastName,
        Integer departmentNo,   // null if the employee has no department
        Integer superId,        // null for the top of the hierarchy
        LocalDate startDate) {
}