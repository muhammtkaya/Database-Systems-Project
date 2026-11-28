package com.etu.expensemanagement.model;

/** One row of expense_management.department (manager_ssn is deliberately not read). */
public record Department(Integer departmentNo, String name, Integer superNo) {
}