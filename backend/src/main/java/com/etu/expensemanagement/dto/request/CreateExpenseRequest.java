package com.etu.expensemanagement.dto.request;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Body of POST /api/expenses, matched to the CURRENT database schema:
 * vendor_id is NOT NULL, there is no category or description column, and the amount must fit
 * price (INT) + decimal_price (0-99). No employeeId and no departmentId: both come from the server.
 */
public record CreateExpenseRequest(

        @NotNull(message = "vendorId is required")
        Integer vendorId,

        @NotNull(message = "amount is required")
        @Positive(message = "amount must be greater than zero")
        @Digits(integer = 9, fraction = 2, message = "amount allows at most 9 integer digits and 2 decimals")
        BigDecimal amount,

        @NotBlank(message = "currencyCode is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "currencyCode must be a 3-letter uppercase code such as TRY")
        String currencyCode,

        @NotNull(message = "expenseDate is required")
        @PastOrPresent(message = "expenseDate cannot be in the future")
        LocalDate expenseDate) {
}
