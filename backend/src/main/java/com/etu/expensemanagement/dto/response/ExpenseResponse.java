package com.etu.expensemanagement.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Bir giderin API gorunumu (hem onay bekleyen hem onaylanmis listelerde kullanilir).
 * Tutar DB'de price + decimal_price olarak iki INT'tir; disariya tek BigDecimal olarak verilir.
 * Composite PK'yi olusturan alanlar (employeeId, departmentId, vendorId, expenseDate, amount)
 * ayni zamanda approve/reject icin gideri tekrar tanimlamaya yeter.
 */
public record ExpenseResponse(
        Integer employeeId,
        String employeeFullName,
        Integer departmentId,
        Integer vendorId,
        String vendorName,
        LocalDate expenseDate,
        BigDecimal amount,
        String currency) {
}
