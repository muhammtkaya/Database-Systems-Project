package com.etu.expensemanagement.service;

import com.etu.expensemanagement.dto.response.EmployeeResponse;
import com.etu.expensemanagement.exception.UnauthorizedException;
import com.etu.expensemanagement.model.Department;
import com.etu.expensemanagement.model.Employee;
import com.etu.expensemanagement.repository.DepartmentRepository;
import com.etu.expensemanagement.repository.EmployeeRepository;
import com.etu.expensemanagement.security.CurrentUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeService {

    private final CurrentUser currentUser;
    private final EmployeeRepository employees;
    private final DepartmentRepository departments;

    public EmployeeService(CurrentUser currentUser, EmployeeRepository employees, DepartmentRepository departments) {
        this.currentUser = currentUser;
        this.employees = employees;
        this.departments = departments;
    }

    /**
     * The employee behind the current request. The dev header only CLAIMS an id,
     * so we verify here that such an employee really exists.
     */
    @Transactional(readOnly = true)
    public Employee requireCurrentEmployee() {
        Long claimed = currentUser.employeeId();
        if (claimed > Integer.MAX_VALUE) {
            throw new UnauthorizedException("Unknown employee");
        }
        return employees.findById(claimed.intValue())
                .orElseThrow(() -> new UnauthorizedException("Unknown employee"));
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getCurrentEmployee() {
        Employee e = requireCurrentEmployee();
        String departmentName = e.departmentNo() == null ? null
                : departments.findById(e.departmentNo()).map(Department::name).orElse(null);
        boolean manager = employees.existsBySuperId(e.employeeId());
        return new EmployeeResponse(e.employeeId(), e.firstName(), e.lastName(),
                e.departmentNo(), departmentName, e.superId(), e.startDate(), manager);
    }
}