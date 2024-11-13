package com.littlepay;

import com.littlepay.domain.Constants;
import com.littlepay.domain.Trip;

public class TripMapper {

    public static String TripToStringMapper(Trip trip) {
        String started = trip.started().format(Constants.FORMATTER);
        String finished = trip.finished() != null ? trip.finished().format(Constants.FORMATTER) : "";
        String chargeAmount = Constants.DECIMAL_FORMAT.format(trip.chargeAmount());
        String duration = trip.durationSec() != null ? trip.durationSec().toString() : "";
        String toStopId = trip.toStopId() != null ? trip.toStopId() : "";

        return String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s", started, finished,
                duration, trip.fromStopId(), toStopId, chargeAmount, trip.companyId(),
                trip.busID(), trip.PAN(), trip.status());
    }
}