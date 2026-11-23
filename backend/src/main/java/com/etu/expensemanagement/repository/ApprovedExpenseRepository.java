package com.etu.expensemanagement.repository;

import com.etu.expensemanagement.entity.ApprovedExpense;
import com.etu.expensemanagement.entity.ExpenseKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ApprovedExpenseRepository extends JpaRepository<ApprovedExpense, ExpenseKey> {

    List<ApprovedExpense> findByIdEmployeeId(Integer employeeId);

    /** Onaylanmis giderler, verilen departmanlara ait olanlar - sayfalanmis (admin menusu). */
    Page<ApprovedExpense> findByIdDepartmentIdIn(Collection<Integer> departmentIds, Pageable pageable);
}
