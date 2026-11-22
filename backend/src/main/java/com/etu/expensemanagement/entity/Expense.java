package com.etu.expensemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * a row here means "waiting for approval"
 */
@Entity
@Table(name = "expense")
public class Expense {

    @EmbeddedId
    private ExpenseKey id;

    @Column(name = "currency", nullable = false, length = 10)
    private String currency;

    protected Expense() {

    }

    public Expense(ExpenseKey id, String currency) {
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
