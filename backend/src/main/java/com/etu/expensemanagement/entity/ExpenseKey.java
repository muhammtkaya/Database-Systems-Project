package com.etu.expensemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * The 6-column composite primary key shared by expense and approved_expense:
 * (employee_id, department_id, vendor_id, date, price, decimal_price).
 * A single expense has no one-column id in the current schema, so the whole key identifies it.
 */
@Embeddable
public class ExpenseKey implements Serializable {

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "department_id", nullable = false)
    private Integer departmentId;

    @Column(name = "vendor_id", nullable = false)
    private Integer vendorId;

    /** Column is literally named "date", so it is quoted. */
    @Column(name = "\"date\"", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "decimal_price", nullable = false)
    private Integer decimalPrice;

    protected ExpenseKey() {
        // required by JPA
    }

    public ExpenseKey(Integer employeeId, Integer departmentId, Integer vendorId,
                      LocalDate expenseDate, Integer price, Integer decimalPrice) {
        this.employeeId = employeeId;
        this.departmentId = departmentId;
        this.vendorId = vendorId;
        this.expenseDate = expenseDate;
        this.price = price;
        this.decimalPrice = decimalPrice;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getDecimalPrice() {
        return decimalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExpenseKey other)) {
            return false;
        }
        return Objects.equals(employeeId, other.employeeId)
                && Objects.equals(departmentId, other.departmentId)
                && Objects.equals(vendorId, other.vendorId)
                && Objects.equals(expenseDate, other.expenseDate)
                && Objects.equals(price, other.price)
                && Objects.equals(decimalPrice, other.decimalPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeId, departmentId, vendorId, expenseDate, price, decimalPrice);
    }
}
