package com.etu.expensemanagement.controller;

import com.etu.expensemanagement.dto.request.CreateExpenseRequest;
import com.etu.expensemanagement.dto.request.DecisionRequest;
import com.etu.expensemanagement.dto.response.ExpenseResponse;
import com.etu.expensemanagement.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /** KULLANICI menusu: yeni gider ac (onay bekleyen olarak kaydedilir). */
    @PostMapping
    public ResponseEntity<ExpenseResponse> create(@Valid @RequestBody CreateExpenseRequest request) {
        ExpenseResponse created = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** ADMIN menusu: yonettigin departmanlarin onay bekleyen giderleri (sayfalanmis, yeniden eskiye). */
    @GetMapping("/pending")
    public Page<ExpenseResponse> pending(
            @PageableDefault(size = 20, sort = "id.expenseDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return expenseService.listPendingForAdmin(pageable);
    }

    /** ADMIN menusu: yonettigin departmanlarin onaylanmis giderleri (sayfalanmis). */
    @GetMapping("/approved")
    public Page<ExpenseResponse> approved(
            @PageableDefault(size = 20, sort = "id.expenseDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return expenseService.listApprovedForAdmin(pageable);
    }

    /** ADMIN: bir gideri onayla (pending -> approved, atomik). */
    @PostMapping("/approve")
    public ResponseEntity<Void> approve(@Valid @RequestBody DecisionRequest request) {
        expenseService.approve(request);
        return ResponseEntity.noContent().build();
    }

    /** ADMIN: bir gideri reddet (pending'den kaldirilir). */
    @PostMapping("/reject")
    public ResponseEntity<Void> reject(@Valid @RequestBody DecisionRequest request) {
        expenseService.reject(request);
        return ResponseEntity.noContent().build();
    }
}
