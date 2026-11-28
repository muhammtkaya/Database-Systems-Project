package com.etu.expensemanagement.service;

import com.etu.expensemanagement.dto.response.VendorResponse;
import com.etu.expensemanagement.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VendorService {

    private final VendorRepository vendors;

    public VendorService(VendorRepository vendors) {
        this.vendors = vendors;
    }

    @Transactional(readOnly = true)
    public List<VendorResponse> listVendors() {
        return vendors.findAllOrderByName().stream()
                .map(v -> new VendorResponse(v.vendorId(), v.name()))
                .toList();
    }
}