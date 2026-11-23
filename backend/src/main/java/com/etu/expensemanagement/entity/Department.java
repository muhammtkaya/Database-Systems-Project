package com.etu.expensemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "department")
public class Department {

    @Id
    @Column(name = "department_no")
    private Integer departmentNo;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** Bu departmanin yoneticisi (employee_id). Null olabilir. "Admin" rolu buradan turetilir. */
    @Column(name = "manager_id")
    private Integer managerId;

    /** Parent department: plain foreign-key value - no object relationship */
    @Column(name = "super_no")
    private Integer superNo;

    protected Department() {

    }

    public Integer getDepartmentNo() {
        return departmentNo;
    }

    public String getName() {
        return name;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public Integer getSuperNo() {
        return superNo;
    }
}
