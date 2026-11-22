package com.etu.expensemanagement.repository;

import com.etu.expensemanagement.entity.Expense;
import com.etu.expensemanagement.entity.ExpenseKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, ExpenseKey> {

    /** All pending expenses of one employee (employeeId is part of the embedded key "id"). */
    List<Expense> findByIdEmployeeId(Integer employeeId);
}
