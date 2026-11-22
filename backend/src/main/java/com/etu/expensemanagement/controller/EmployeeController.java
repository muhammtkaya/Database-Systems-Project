package com.etu.expensemanagement.controller;

import com.etu.expensemanagement.dto.response.EmployeeResponse;
import com.etu.expensemanagement.service.EmployeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /** "Who am I?" - the employee identified for this request */
    @GetMapping("/me")
    public EmployeeResponse me() {
        return employeeService.getCurrentEmployee();
    }
}
