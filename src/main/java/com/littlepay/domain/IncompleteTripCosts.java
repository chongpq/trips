package com.littlepay.domain;

import java.math.BigDecimal;

import static com.littlepay.domain.Constants.STOP_ID_MAP;

public class IncompleteTripCosts {

    private final BigDecimal[] tripCosts = { new BigDecimal("7.30"), new BigDecimal("5.50"), new BigDecimal("7.30") };

    /**
     *  This is an in-memory version of incompleted trip costs
     *  we could read from a configuration file to setup <code>BigDecimal[] tripCosts</code>
     */
    public IncompleteTripCosts() {

    }

    public BigDecimal get(String stopId) {
        if (!STOP_ID_MAP.containsKey(stopId)) {
            throw new IllegalArgumentException("Invalid stopId " + stopId);
        }
        return tripCosts[STOP_ID_MAP.get(stopId)];
    }
}
