package com.etu.expensemanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Table expense_management.vendor. */
@Entity
@Table(name = "vendor")
public class Vendor {

    @Id
    @Column(name = "vendor_id")
    private Integer vendorId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    protected Vendor() {
        // required by JPA
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public String getName() {
        return name;
    }
}
