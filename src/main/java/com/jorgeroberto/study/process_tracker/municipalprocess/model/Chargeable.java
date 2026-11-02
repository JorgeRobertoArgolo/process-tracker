package com.jorgeroberto.study.process_tracker.municipalprocess.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

public interface Chargeable {
    BigDecimal calculateIssuanceFee();

    default boolean isFeeExempt() {
        BigDecimal issuanceFee = calculateIssuanceFee();

        return issuanceFee.compareTo(BigDecimal.ZERO) == 0;
    }

    static BigDecimal sumFees(final Collection<? extends Chargeable> items) {
        BigDecimal sum = BigDecimal.ZERO;

        for (Chargeable item : items) {
            sum = sum.add(item.calculateIssuanceFee());
        }

        return toMonetaryScale(sum);
    }

    private static BigDecimal toMonetaryScale(final BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_EVEN);
    }
}
