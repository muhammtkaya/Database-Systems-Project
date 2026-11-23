package com.etu.expensemanagement.repository;

import com.etu.expensemanagement.entity.Expense;
import com.etu.expensemanagement.entity.ExpenseKey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, ExpenseKey> {

    /** All pending expenses of one employee (employeeId is part of the embedded key "id"). */
    List<Expense> findByIdEmployeeId(Integer employeeId);

    /**
     * Onay bekleyen giderler, verilen departmanlara ait olanlar - sayfalanmis.
     * Admin menusu; deptIds = adminin yonettigi departmanlar. 250k+ satir icin LIMIT sart.
     */
    Page<Expense> findByIdDepartmentIdIn(Collection<Integer> departmentIds, Pageable pageable);
}
