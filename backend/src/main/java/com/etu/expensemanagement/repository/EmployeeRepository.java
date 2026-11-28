package com.etu.expensemanagement.repository;

import com.etu.expensemanagement.model.Employee;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class EmployeeRepository {

    private final JdbcClient jdbc;

    public EmployeeRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Turns one result row into an Employee. getObject(..., Integer.class) returns null for SQL NULL. */
    private static Employee map(ResultSet rs, int rowNum) throws SQLException {
        return new Employee(
                rs.getInt("employee_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getObject("department_no", Integer.class),
                rs.getObject("super_id", Integer.class),
                rs.getObject("start_date", LocalDate.class));
    }

    /** Used by: GET /api/employees/me (and every endpoint that must verify the caller exists). */
    public Optional<Employee> findById(int employeeId) {
        return jdbc.sql("""
                        SELECT employee_id, first_name, last_name, department_no, super_id, start_date
                        FROM expense_management.employee
                        WHERE employee_id = :id
                        """)
                .param("id", employeeId)
                .query(EmployeeRepository::map)
                .optional();
    }

    /** Direct reports of a supervisor. Used later by the manager features. */
    public List<Employee> findBySuperId(int superId) {
        return jdbc.sql("""
                        SELECT employee_id, first_name, last_name, department_no, super_id, start_date
                        FROM expense_management.employee
                        WHERE super_id = :superId
                        ORDER BY employee_id
                        """)
                .param("superId", superId)
                .query(EmployeeRepository::map)
                .list();
    }

    /** A manager is anyone who has at least one direct report. Used by: GET /api/employees/me. */
    public boolean existsBySuperId(int superId) {
        return jdbc.sql("""
                        SELECT EXISTS (
                            SELECT 1 FROM expense_management.employee WHERE super_id = :id
                        )
                        """)
                .param("id", superId)
                .query(Boolean.class)
                .single();
    }
}