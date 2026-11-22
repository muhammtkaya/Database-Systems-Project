package com.etu.expensemanagement.db;

import com.etu.expensemanagement.entity.ApprovedExpense;
import com.etu.expensemanagement.entity.Employee;
import com.etu.expensemanagement.entity.Expense;
import com.etu.expensemanagement.repository.ApprovedExpenseRepository;
import com.etu.expensemanagement.repository.DepartmentRepository;
import com.etu.expensemanagement.repository.EmployeeRepository;
import com.etu.expensemanagement.repository.ExpenseRepository;
import com.etu.expensemanagement.repository.VendorRepository;
import com.etu.expensemanagement.util.AmountConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class SchemaMappingIT {

    @Autowired DepartmentRepository departments;
    @Autowired EmployeeRepository employees;
    @Autowired VendorRepository vendors;
    @Autowired ExpenseRepository expenses;
    @Autowired ApprovedExpenseRepository approvedExpenses;

    @Test
    void referenceTablesAreReadable() {
        assertThat(departments.count()).isGreaterThanOrEqualTo(5);
        assertThat(employees.count()).isGreaterThanOrEqualTo(20);
        assertThat(vendors.count()).isGreaterThanOrEqualTo(20);
    }

    @Test
    void hierarchyIsReadFromSuperId() {
        assertThat(employees.findById(101).orElseThrow().getSuperId()).isNull();
        assertThat(employees.findById(109).orElseThrow().getSuperId()).isEqualTo(105);

        List<Integer> reportsOf105 = employees.findBySuperId(105).stream().map(Employee::getEmployeeId).toList();
        assertThat(reportsOf105).containsExactlyInAnyOrder(109, 110, 111);

        assertThat(employees.existsBySuperId(105)).isTrue();
        assertThat(employees.existsBySuperId(109)).isFalse();
    }

    @Test
    void pendingExpensesMapWithCompositeKeyAndAmountConverts() {
        List<Expense> rows = expenses.findByIdEmployeeId(101);
        assertThat(rows).isNotEmpty();
        for (Expense e : rows) {
            assertThat(e.getId().getDepartmentId()).isEqualTo(1); // employee 101 works in department 1
            assertThat(AmountConverter.toAmount(e.getId().getPrice(), e.getId().getDecimalPrice()).signum()).isPositive();
            assertThat(e.getCurrency()).isNotBlank();
        }
    }

    @Test
    void approvedExpensesAreReadable() {
        assertThat(approvedExpenses.count()).isGreaterThan(0);
        List<ApprovedExpense> rows = approvedExpenses.findByIdEmployeeId(101);
        assertThat(rows).allSatisfy(a -> assertThat(a.getId().getExpenseDate()).isNotNull());
    }
}
