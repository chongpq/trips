package com.littlepay;

import com.littlepay.domain.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.littlepay.domain.Constants.FORMATTER;
import static java.time.LocalDateTime.*;
import static org.junit.jupiter.api.Assertions.*;

class TripCalculatorTest {

    @Test
    void test_completeTrip() {
        CompletedTripCosts completedTripCosts = new CompletedTripCosts();
        TripCalculator classUnderTest = new TripCalculator(null, completedTripCosts);
        Tap start = new Tap("1", FORMATTER.parse("22-01-2023 13:00:00", LocalDateTime::from), TapType.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap end = new Tap("2", FORMATTER.parse("22-01-2023 13:30:00", LocalDateTime::from), TapType.OFF, "Stop3", "Company1", "Bus37", "5500005555555559");

        Trip trip = classUnderTest.createTrip(start, end);

        assertEquals(of(2023,1,22,13,0,0), trip.started());
        assertEquals(of(2023,1,22,13,30,0), trip.finished());
        assertEquals(1800L, trip.durationSec());
        assertEquals("Stop1", trip.fromStopId());
        assertEquals("Stop3", trip.toStopId());
        assertEquals(new BigDecimal("7.30"), trip.chargeAmount());
        assertEquals("Company1", trip.companyId());
        assertEquals("Bus37", trip.busID());
        assertEquals("5500005555555559", trip.PAN());
        assertEquals(TripStatus.COMPLETED, trip.status());
    }

    @Test
    void test_incompleteTrip() {
        IncompleteTripCosts incompleteTripCosts = new IncompleteTripCosts();
        TripCalculator classUnderTest = new TripCalculator(incompleteTripCosts, null);
        Tap start = new Tap("1", FORMATTER.parse("22-01-2023 13:00:00", LocalDateTime::from), TapType.ON, "Stop1", "Company1", "Bus37", "5500005555555559");

        Trip trip = classUnderTest.createIncompletedTrip(start);

        assertEquals(of(2023,1,22,13,0,0), trip.started());
        assertNull(trip.finished());
        assertNull(trip.durationSec());
        assertEquals("Stop1", trip.fromStopId());
        assertNull(trip.toStopId());
        assertEquals(new BigDecimal("7.30"), trip.chargeAmount());
        assertEquals("Company1", trip.companyId());
        assertEquals("Bus37", trip.busID());
        assertEquals("5500005555555559", trip.PAN());
        assertEquals(TripStatus.INCOMPLETED, trip.status());
    }

    @Test
    void test_cancelledTrip() {
        CompletedTripCosts completedTripCosts = new CompletedTripCosts();
        TripCalculator classUnderTest = new TripCalculator(null, completedTripCosts);
        Tap start = new Tap("1", FORMATTER.parse("22-01-2023 13:00:00", LocalDateTime::from), TapType.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap end = new Tap("2", FORMATTER.parse("22-01-2023 13:00:05", LocalDateTime::from), TapType.OFF, "Stop1", "Company1", "Bus37", "5500005555555559");

        Trip trip = classUnderTest.createTrip(start, end);

        assertEquals(of(2023,1,22,13,0,0), trip.started());
        assertEquals(of(2023,1,22,13,0,5), trip.finished());
        assertEquals(5L, trip.durationSec());
        assertEquals("Stop1", trip.fromStopId());
        assertEquals("Stop1", trip.toStopId());
        assertEquals(BigDecimal.ZERO, trip.chargeAmount());
        assertEquals("Company1", trip.companyId());
        assertEquals("Bus37", trip.busID());
        assertEquals("5500005555555559", trip.PAN());
        assertEquals(TripStatus.CANCELLED, trip.status());
    }

}