package com.etu.expensemanagement.repository;

import com.etu.expensemanagement.model.Department;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class DepartmentRepository {

    private final JdbcClient jdbc;

    public DepartmentRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    private static Department map(ResultSet rs, int rowNum) throws SQLException {
        return new Department(
                rs.getInt("department_no"),
                rs.getString("name"),
                rs.getObject("super_no", Integer.class));
    }

    /** Used by: GET /api/employees/me (to show the department name). */
    public Optional<Department> findById(int departmentNo) {
        return jdbc.sql("""
                SELECT department_no, name, super_no
                FROM expense_management.department
                WHERE department_no = :no
                """)
                .param("no", departmentNo)
                .query(DepartmentRepository::map)
                .optional();
    }
}