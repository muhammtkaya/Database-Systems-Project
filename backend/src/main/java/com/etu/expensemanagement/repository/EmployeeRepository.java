package com.etu.expensemanagement.repository;

import com.etu.expensemanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    /** Direct reports of a supervisor. Spring builds the SQL from the method name. */
    List<Employee> findBySuperId(Integer superId);

    /** True when someone reports to this employee, i.e. the employee is a manager. */
    boolean existsBySuperId(Integer superId);
}
