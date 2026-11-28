package com.etu.expensemanagement.db;

import com.etu.expensemanagement.model.Employee;
import com.etu.expensemanagement.repository.DepartmentRepository;
import com.etu.expensemanagement.repository.EmployeeRepository;
import com.etu.expensemanagement.repository.VendorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** Runs every hand-written SQL statement against the real database (mvn verify). */
@SpringBootTest
class SqlRepositoryIT {

    @Autowired EmployeeRepository employees;
    @Autowired DepartmentRepository departments;
    @Autowired VendorRepository vendors;

    @Test
    void vendorsAreReadFromDatabase() {
        var all = vendors.findAllOrderByName();
        assertThat(all).hasSizeGreaterThanOrEqualTo(20);
        List<String> names = all.stream().map(v -> v.name()).toList();
        assertThat(names).doesNotHaveDuplicates().allSatisfy(n -> assertThat(n).isNotBlank());
    }

    @Test
    void departmentIsReadWithParent() {
        var d = departments.findById(5).orElseThrow();
        assertThat(d.name()).isNotBlank();
        assertThat(d.superNo()).isEqualTo(2);
        assertThat(departments.findById(1).orElseThrow().superNo()).isNull();
    }

    @Test
    void employeeIsReadWithNullableColumns() {
        Employee top = employees.findById(101).orElseThrow();
        assertThat(top.superId()).isNull();
        assertThat(top.startDate()).isNotNull();
        assertThat(employees.findById(109).orElseThrow().superId()).isEqualTo(105);
        assertThat(employees.findById(999)).isEmpty();
    }

    @Test
    void hierarchyQueries() {
        List<Integer> reports = employees.findBySuperId(105).stream().map(Employee::employeeId).toList();
        assertThat(reports).containsExactly(109, 110, 111);
        assertThat(employees.existsBySuperId(105)).isTrue();
        assertThat(employees.existsBySuperId(109)).isFalse();
    }
}