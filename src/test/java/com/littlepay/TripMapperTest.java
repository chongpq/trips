package com.littlepay;

import com.littlepay.domain.Trip;
import com.littlepay.domain.TripStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static java.time.LocalDateTime.of;
import static org.junit.jupiter.api.Assertions.*;

class TripMapperTest {

    @Test
    void tripToStringMapper_COMPLETE() {
        Trip input = new Trip(of(2023,1,22,13,0,0),
                of(2023,1,22,13,30,0),
                1800L, "Stop1", "Stop3", new BigDecimal("7.30"),
                "Company1", "Bus37", "5500005555555559", TripStatus.COMPLETED);
        assertEquals("22-01-2023 13:00:00, 22-01-2023 13:30:00, 1800, Stop1, Stop3, $7.30, Company1, Bus37, 5500005555555559, COMPLETED", TripMapper.TripToStringMapper(input));
    }

    @Test
    void tripToStringMapper_INCOMPLETE() {
        Trip input = new Trip(of(2023,1,22,13,0,0),
                null, null, "Stop1", null, new BigDecimal("7.30"),
                "Company1", "Bus37", "5500005555555559", TripStatus.INCOMPLETED);
        assertEquals("22-01-2023 13:00:00, , , Stop1, , $7.30, Company1, Bus37, 5500005555555559, INCOMPLETED", TripMapper.TripToStringMapper(input));
    }

    @Test
    void tripToStringMapper_CANCELLED() {
        Trip input = new Trip(of(2023,1,22,13,0,0),
                of(2023,1,22,13,0,5),
                5L, "Stop1", "Stop1", BigDecimal.ZERO,
                "Company1", "Bus37", "5500005555555559", TripStatus.CANCELLED);
        assertEquals("22-01-2023 13:00:00, 22-01-2023 13:00:05, 5, Stop1, Stop1, $.00, Company1, Bus37, 5500005555555559, CANCELLED", TripMapper.TripToStringMapper(input));
    }
}