package com.littlepay.domain;

import java.math.BigDecimal;

import static com.littlepay.domain.Constants.STOP_ID_MAP;

public class CompletedTripCosts {

    private final BigDecimal[][] tripCosts = {
            {BigDecimal.ZERO, new BigDecimal("3.25"), new BigDecimal("7.30")},
            {new BigDecimal("3.25"), BigDecimal.ZERO, new BigDecimal("5.50")},
            {new BigDecimal("7.30"), new BigDecimal("5.50"), BigDecimal.ZERO}
    };

    /**
     *  This is a in-memory version of completed trip costs
     *  we could read from a configuration file to setup <code>BigDecimal[][] tripCosts</code>
     */
    public CompletedTripCosts() {

    }

    public BigDecimal get(String start, String end) {
        if (!STOP_ID_MAP.containsKey(start) || !STOP_ID_MAP.containsKey(end)) {
            throw new IllegalArgumentException("Invalid StopId");
        }

        return tripCosts[STOP_ID_MAP.get(start)][STOP_ID_MAP.get(end)];
    }
}
