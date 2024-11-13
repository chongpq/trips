package com.littlepay.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Trip(LocalDateTime started, LocalDateTime finished, Long durationSec, String fromStopId, String toStopId,
                   BigDecimal chargeAmount, String companyId, String busID, String PAN, TripStatus status) {

}
