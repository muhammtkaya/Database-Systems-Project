package com.etu.expensemanagement.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Approve / reject islemlerinin govdesi.
 * Giderin tek bir yuzeysel id'si olmadigi icin (composite PK), hedef gider
 * onay bekleyen listesinden gelen alanlarla yeniden tanimlanir. departmentId'yi de
 * client gonderir; ama yetki her zaman sunucuda yeniden dogrulanir (defense in depth).
 */
public record DecisionRequest(

        @NotNull(message = "employeeId is required")
        Integer employeeId,

        @NotNull(message = "departmentId is required")
        Integer departmentId,

        @NotNull(message = "vendorId is required")
        Integer vendorId,

        @NotNull(message = "expenseDate is required")
        LocalDate expenseDate,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        @Digits(integer = 9, fraction = 2, message = "amount allows at most 9 integer digits and 2 decimals")
        BigDecimal amount,

        @Size(max = 500, message = "comment must be at most 500 characters")
        String comment) {
}
