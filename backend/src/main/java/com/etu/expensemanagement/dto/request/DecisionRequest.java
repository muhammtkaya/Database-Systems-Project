package com.etu.expensemanagement.dto.request;

import jakarta.validation.constraints.Size;

/** Optional body of the approve / reject endpoints. */
public record DecisionRequest(

        @Size(max = 500, message = "comment must be at most 500 characters")
        String comment) {
}
