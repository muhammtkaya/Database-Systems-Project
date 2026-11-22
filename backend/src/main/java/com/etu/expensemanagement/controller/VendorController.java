package com.etu.expensemanagement.controller;

import com.etu.expensemanagement.dto.response.VendorResponse;
import com.etu.expensemanagement.service.VendorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping
    public List<VendorResponse> list() {
        return vendorService.listVendors();
    }
}
