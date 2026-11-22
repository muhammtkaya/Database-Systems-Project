package com.etu.expensemanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

/** error shape every failing endpoint returns */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldIssue> fieldErrors) {

    public record FieldIssue(String field, String message) {
    }

    public static ErrorResponse of(HttpStatus status, String message, String path, List<FieldIssue> fieldErrors) {
        return new ErrorResponse(Instant.now(), status.value(), status.getReasonPhrase(), message, path, fieldErrors);
    }
}
