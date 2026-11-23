package com.etu.expensemanagement.repository;

import com.etu.expensemanagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    /** Bu employee'nin yonetici oldugu departmanlar. Bos ise employee ADMIN degildir. */
    List<Department> findByManagerId(Integer managerId);
}
