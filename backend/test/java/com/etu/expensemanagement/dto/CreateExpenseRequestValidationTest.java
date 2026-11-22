package com.etu.expensemanagement.dto;

import com.etu.expensemanagement.dto.request.CreateExpenseRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CreateExpenseRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    private static CreateExpenseRequest request(Integer vendorId, String amount, String currency, LocalDate date) {
        return new CreateExpenseRequest(vendorId, amount == null ? null : new BigDecimal(amount), currency, date);
    }

    private static Set<String> invalidFields(CreateExpenseRequest r) {
        return validator.validate(r).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Object::toString)
                .collect(Collectors.toSet());
    }

    @Test
    void validRequest_hasNoViolations() {
        assertThat(validator.validate(request(2, "125.50", "TRY", LocalDate.now().minusDays(1)))).isEmpty();
    }

    @Test
    void missingVendor_isRejected() {
        assertThat(invalidFields(request(null, "10", "TRY", LocalDate.now()))).containsExactly("vendorId");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.00", "-5", "-0.01"})
    void zeroOrNegativeAmount_isRejected(String amount) {
        assertThat(invalidFields(request(1, amount, "TRY", LocalDate.now()))).containsExactly("amount");
    }

    @Test
    void tooManyDecimals_isRejected() {
        assertThat(invalidFields(request(1, "10.123", "TRY", LocalDate.now()))).containsExactly("amount");
    }

    @Test
    void tooManyIntegerDigits_isRejected() {
        assertThat(invalidFields(request(1, "1234567890", "TRY", LocalDate.now()))).containsExactly("amount");
    }

    @Test
    void missingAmount_isRejected() {
        assertThat(invalidFields(request(1, null, "TRY", LocalDate.now()))).containsExactly("amount");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "tr", "try", "TURK", "T1Y"})
    void badCurrencyCode_isRejected(String code) {
        assertThat(invalidFields(request(1, "10", code, LocalDate.now()))).contains("currencyCode");
    }

    @Test
    void futureDate_isRejected() {
        assertThat(invalidFields(request(1, "10", "TRY", LocalDate.now().plusDays(1)))).containsExactly("expenseDate");
    }

    @Test
    void missingDate_isRejected() {
        assertThat(invalidFields(request(1, "10", "TRY", null))).containsExactly("expenseDate");
    }
}
