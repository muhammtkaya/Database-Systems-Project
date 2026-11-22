package com.etu.expensemanagement.util;

import com.etu.expensemanagement.exception.BadRequestException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * The database stores an amount as two integers: price (whole units) and decimal_price (0-99 hundredths).
 * The rest of the application uses one BigDecimal
 */
public final class AmountConverter {

    private AmountConverter() {
    }

    public record AmountParts(int price, int decimalPrice) {
    }

    /** Database values -> BigDecimal with exactly 2 decimals. */
    public static BigDecimal toAmount(int price, int decimalPrice) {
        if (price < 0 || decimalPrice < 0 || decimalPrice > 99) {
            throw new IllegalArgumentException("Invalid stored amount parts: " + price + " / " + decimalPrice);
        }
        return BigDecimal.valueOf(price).add(BigDecimal.valueOf(decimalPrice, 2));
    }

    /** Client amount -> database values. Rejects zero, negatives, more than 2 decimals, and values too big for INT. */
    public static AmountParts toParts(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new BadRequestException("amount must be greater than zero");
        }
        BigDecimal scaled;
        try {
            scaled = amount.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException e) {
            throw new BadRequestException("amount allows at most 2 decimal places");
        }
        BigInteger[] wholeAndCents = scaled.unscaledValue().divideAndRemainder(BigInteger.valueOf(100));
        if (wholeAndCents[0].bitLength() > 31) {
            throw new BadRequestException("amount is too large");
        }
        return new AmountParts(wholeAndCents[0].intValue(), wholeAndCents[1].intValue());
    }
}
