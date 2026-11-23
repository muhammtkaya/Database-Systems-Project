package com.etu.expensemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/** Table expense_management.employee. Foreign keys are kept as plain id values on purpose. */
@Entity
@Table(name = "employee")
public class Employee {

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    /** Nullable in the database: an employee may have no department. */
    @Column(name = "department_no")
    private Integer departmentNo;

    /** Direct supervisor; null for the top of the hierarchy. */
    @Column(name = "super_id")
    private Integer superId;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /** Giris icin e-posta (tanimlayici). Auth akisi henuz kurulmadi; kolon hazir. */
    @Column(name = "email", length = 100, unique = true)
    private String email;

    /** Sifrenin KENDISI degil, tek yonlu hash (BCrypt/Argon2id). Auth fazinda kullanilacak. */
    @Column(name = "password_hash", length = 100)
    private String passwordHash;

    protected Employee() {
        // required by JPA
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Integer getDepartmentNo() {
        return departmentNo;
    }

    public Integer getSuperId() {
        return superId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}
