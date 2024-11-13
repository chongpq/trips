package com.littlepay;

import java.math.BigDecimal;
import java.time.Duration;

import com.littlepay.domain.*;

public class TripCalculator {

    private final IncompleteTripCosts incompleteTripCosts;
    private final CompletedTripCosts completedTripCosts;

    public TripCalculator(IncompleteTripCosts incompleteTripCosts, CompletedTripCosts completedTripCosts) {
        this.incompleteTripCosts = incompleteTripCosts;
        this.completedTripCosts = completedTripCosts;
    }

    public Trip calculate(Tap start, Tap end) {
        Long duration = Duration.between(start.dateTimeUTC(), end.dateTimeUTC()).getSeconds();
        TripStatus status = start.stopId().equals(end.stopId()) ? TripStatus.CANCELLED : TripStatus.COMPLETED;
        BigDecimal chargeAmount = completedTripCosts.get(start.stopId(), end.stopId());

        return new Trip(start.dateTimeUTC(), end.dateTimeUTC(), duration, start.stopId(),
                end.stopId(), chargeAmount, start.companyId(),
                start.busID(), start.PAN(), status);
    }

    public Trip createIncompletedTrip(Tap start) {
        return new Trip(start.dateTimeUTC(), null, null, start.stopId(),
                null, incompleteTripCosts.get(start.stopId()), start.companyId(),
                start.busID(), start.PAN(), TripStatus.INCOMPLETED);
    }
}
