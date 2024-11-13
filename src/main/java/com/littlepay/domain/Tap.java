package com.littlepay.domain;

import java.time.LocalDateTime;

public record Tap(String ID, LocalDateTime dateTimeUTC, TapType tapType, String stopId, String companyId, String busID, String PAN) {

}
