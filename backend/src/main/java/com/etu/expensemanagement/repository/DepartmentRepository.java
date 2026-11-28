package com.etu.expensemanagement.repository;

import com.etu.expensemanagement.model.Department;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class DepartmentRepository {

    private final JdbcClient jdbc;

    public DepartmentRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Used by: GET /api/employees/me (to show the department name). */
    public Optional<Department> findById(int departmentNo) {
        return jdbc.sql("""
                        SELECT department_no, name, super_no
                        FROM expense_management.department
                        WHERE department_no = :no
                        """)
                .param("no", departmentNo)
                .query((rs, rowNum) -> new Department(
                        rs.getInt("department_no"),
                        rs.getString("name"),
                        rs.getObject("super_no", Integer.class)))
                .optional();
    }
}