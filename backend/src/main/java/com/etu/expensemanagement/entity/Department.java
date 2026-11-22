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

    public Integer getSuperNo() {
        return superNo;
    }
}
