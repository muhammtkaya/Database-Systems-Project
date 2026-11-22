package com.etu.expensemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/** same columns as expense - a row means "approved". */
@Entity
@Table(name = "approved_expense")
public class ApprovedExpense {

    @EmbeddedId
    private ExpenseKey id;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    protected ApprovedExpense() {
        // required by JPA
    }

    public ApprovedExpense(ExpenseKey id, String currency) {
        this.id = id;
        this.currency = currency;
    }

    public ExpenseKey getId() {
        return id;
    }

    public String getCurrency() {
        return currency;
    }
}
