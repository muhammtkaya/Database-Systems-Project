package com.etu.expensemanagement.util;

import com.etu.expensemanagement.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AmountConverterTest {

    @ParameterizedTest
    @CsvSource({
            "125, 5, 125.05",
            "125, 50, 125.50",
            "0, 99, 0.99",
            "1683, 57, 1683.57",
            "10, 0, 10.00"
    })
    void toAmount_combinesWholeAndHundredths(int price, int decimal, String expected) {
        BigDecimal result = AmountConverter.toAmount(price, decimal);
        assertThat(result).isEqualByComparingTo(expected);
        assertThat(result.scale()).isEqualTo(2);
    }

    @Test
    void toAmount_rejectsInvalidStoredParts() {
        assertThatThrownBy(() -> AmountConverter.toAmount(10, 100)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AmountConverter.toAmount(10, -1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> AmountConverter.toAmount(-1, 10)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @CsvSource({
            "125.50, 125, 50",
            "125.5, 125, 50",
            "125.05, 125, 5",
            "0.07, 0, 7",
            "10, 10, 0",
            "10.00, 10, 0",
            "10.1000, 10, 10",
            "2147483647.99, 2147483647, 99"
    })
    void toParts_splitsAmount(String amount, int price, int decimal) {
        AmountConverter.AmountParts parts = AmountConverter.toParts(new BigDecimal(amount));
        assertThat(parts.price()).isEqualTo(price);
        assertThat(parts.decimalPrice()).isEqualTo(decimal);
    }

    @ParameterizedTest
    @CsvSource({"0", "0.00", "-1", "-0.01", "10.001", "2147483648"})
    void toParts_rejectsBadAmounts(String amount) {
        assertThatThrownBy(() -> AmountConverter.toParts(new BigDecimal(amount)))
                .isInstanceOf(BadRequestException.class);
    }

    @Test
    void toParts_rejectsNull() {
        assertThatThrownBy(() -> AmountConverter.toParts(null)).isInstanceOf(BadRequestException.class);
    }

    @Test
    void roundTrip_keepsTheAmount() {
        BigDecimal original = new BigDecimal("4321.09");
        AmountConverter.AmountParts parts = AmountConverter.toParts(original);
        assertThat(AmountConverter.toAmount(parts.price(), parts.decimalPrice())).isEqualByComparingTo(original);
    }
}
