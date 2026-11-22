package com.etu.expensemanagement.repository;

import com.etu.expensemanagement.entity.ApprovedExpense;
import com.etu.expensemanagement.entity.ExpenseKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApprovedExpenseRepository extends JpaRepository<ApprovedExpense, ExpenseKey> {

    List<ApprovedExpense> findByIdEmployeeId(Integer employeeId);
}
