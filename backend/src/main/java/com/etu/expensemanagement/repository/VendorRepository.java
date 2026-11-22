package com.etu.expensemanagement.repository;

import com.etu.expensemanagement.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendorRepository extends JpaRepository<Vendor, Integer> {
}
