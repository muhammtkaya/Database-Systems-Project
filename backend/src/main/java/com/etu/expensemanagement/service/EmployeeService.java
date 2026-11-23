package com.etu.expensemanagement.service;

import com.etu.expensemanagement.dto.response.EmployeeResponse;
import com.etu.expensemanagement.entity.Employee;
import com.etu.expensemanagement.exception.UnauthorizedException;
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
     * we verify here that such an employee really exists
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
        String departmentName = e.getDepartmentNo() == null ? null
                : departments.findById(e.getDepartmentNo()).map(d -> d.getName()).orElse(null);
        // ADMIN = bir departmanin yoneticisi olmak (altinda eleman olmasi DEGIL).
        boolean manager = !departments.findByManagerId(e.getEmployeeId()).isEmpty();
        return new EmployeeResponse(e.getEmployeeId(), e.getFirstName(), e.getLastName(),
                e.getDepartmentNo(), departmentName, e.getSuperId(), e.getStartDate(), manager);
    }
}
