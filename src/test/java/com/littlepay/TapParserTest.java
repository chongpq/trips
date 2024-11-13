package com.littlepay;

import com.littlepay.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.littlepay.domain.Constants.FORMATTER;
import static org.junit.jupiter.api.Assertions.*;

class TapParserTest {


    private TapParser tapParser;

    @BeforeEach
    void setup() {
        IncompleteTripCosts incompleteTripCosts = new IncompleteTripCosts();
        CompletedTripCosts completedTripCosts = new CompletedTripCosts();
        TripCalculator tripCalculator = new TripCalculator(incompleteTripCosts, completedTripCosts);
        this.tapParser = new TapParser(tripCalculator);
    }

    @Test
    void emptyTapList() {
        List<Tap> taps = Collections.emptyList();

        List<Trip> trips = tapParser.getTrips(taps);

        assertEquals(Collections.emptyList(), trips);
    }

    @Test
    void oneTap_INCOMPLETED() {
        Tap tap = new Tap("1", FORMATTER.parse("22-01-2023 13:00:00", LocalDateTime::from), TapType.ON, "Stop1", "Company1", "Bus37", "5500005555555559");

        List<Tap> taps = List.of(tap);

        List<Trip> trips = tapParser.getTrips(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.getFirst();

        assertEquals(LocalDateTime.of(2023,1,22,13,0,0), trip.started());
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
    void twoTaps_COMPLETED() {
        Tap tap = new Tap("1", FORMATTER.parse("22-01-2023 13:00:00", LocalDateTime::from), TapType.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap tap2 = new Tap("2", FORMATTER.parse("22-01-2023 13:30:00", LocalDateTime::from), TapType.OFF, "Stop3", "Company1", "Bus37", "5500005555555559");

        List<Tap> taps = List.of(tap, tap2);

        List<Trip> trips = tapParser.getTrips(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.getFirst();

        assertEquals(LocalDateTime.of(2023,1,22,13,0,0), trip.started());
        assertEquals(LocalDateTime.of(2023,1,22,13,30,0), trip.finished());
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
    void twoTaps_CANCELLED() {
        Tap tap = new Tap("1", FORMATTER.parse("22-01-2023 13:00:00", LocalDateTime::from), TapType.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap tap2 = new Tap("2", FORMATTER.parse("22-01-2023 13:30:00", LocalDateTime::from), TapType.OFF, "Stop1", "Company1", "Bus37", "5500005555555559");

        List<Tap> taps = List.of(tap, tap2);

        List<Trip> trips = tapParser.getTrips(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.getFirst();

        assertEquals(LocalDateTime.of(2023,1,22,13,0,0), trip.started());
        assertEquals(LocalDateTime.of(2023,1,22,13,30,0), trip.finished());
        assertEquals(1800L, trip.durationSec());
        assertEquals("Stop1", trip.fromStopId());
        assertEquals("Stop1", trip.toStopId());
        assertEquals(BigDecimal.ZERO, trip.chargeAmount());
        assertEquals("Company1", trip.companyId());
        assertEquals("Bus37", trip.busID());
        assertEquals("5500005555555559", trip.PAN());
        assertEquals(TripStatus.CANCELLED, trip.status());
    }

    @Test
    void MultiPANs() {
        Tap tap = new Tap("1", FORMATTER.parse("22-01-2023 13:00:00", LocalDateTime::from), TapType.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap tap2 = new Tap("2", FORMATTER.parse("22-01-2023 13:30:00", LocalDateTime::from), TapType.OFF, "Stop3", "Company1", "Bus37", "5500005555555559");
        Tap tap3 = new Tap("3", FORMATTER.parse("22-01-2023 13:00:00", LocalDateTime::from), TapType.ON, "Stop1", "Company1", "Bus37", "4111111111111111");
        Tap tap4 = new Tap("3", FORMATTER.parse("22-01-2023 13:00:00", LocalDateTime::from), TapType.ON, "Stop1", "Company1", "Bus37", "4111111111111144");

        List<Tap> taps = List.of(tap, tap2, tap3, tap4);

        List<Trip> trips = tapParser.getTrips(taps);

        assertEquals(3, trips.size());
        Trip trip = trips.get(1);

        assertEquals(LocalDateTime.of(2023,1,22,13,0,0), trip.started());
        assertEquals(LocalDateTime.of(2023,1,22,13,30,0), trip.finished());
        assertEquals(1800L, trip.durationSec());
        assertEquals("Stop1", trip.fromStopId());
        assertEquals("Stop3", trip.toStopId());
        assertEquals(new BigDecimal("7.30"), trip.chargeAmount());
        assertEquals("Company1", trip.companyId());
        assertEquals("Bus37", trip.busID());
        assertEquals("5500005555555559", trip.PAN());
        assertEquals(TripStatus.COMPLETED, trip.status());

        trip = trips.get(2);
        assertEquals(LocalDateTime.of(2023,1,22,13,0,0), trip.started());
        assertNull(trip.finished());
        assertNull(trip.durationSec());
        assertEquals("Stop1", trip.fromStopId());
        assertNull(trip.toStopId());
        assertEquals(new BigDecimal("7.30"), trip.chargeAmount());
        assertEquals("Company1", trip.companyId());
        assertEquals("Bus37", trip.busID());
        assertEquals("4111111111111111", trip.PAN());
        assertEquals(TripStatus.INCOMPLETED, trip.status());

        trip = trips.get(0);
        assertEquals(LocalDateTime.of(2023,1,22,13,0,0), trip.started());
        assertNull(trip.finished());
        assertNull(trip.durationSec());
        assertEquals("Stop1", trip.fromStopId());
        assertNull(trip.toStopId());
        assertEquals(new BigDecimal("7.30"), trip.chargeAmount());
        assertEquals("Company1", trip.companyId());
        assertEquals("Bus37", trip.busID());
        assertEquals("4111111111111144", trip.PAN());
        assertEquals(TripStatus.INCOMPLETED, trip.status());
    }

    @Test
    void INCOMPLETED_first_then_process_the_rest() {
        Tap tap = new Tap("1", FORMATTER.parse("22-01-2023 13:00:00", LocalDateTime::from), TapType.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap tap2 = new Tap("2", FORMATTER.parse("22-01-2023 13:30:00", LocalDateTime::from), TapType.ON, "Stop3", "Company1", "Bus37", "5500005555555559");
        Tap tap3 = new Tap("3", FORMATTER.parse("22-01-2023 13:40:00", LocalDateTime::from), TapType.OFF, "Stop3", "Company1", "Bus37", "5500005555555559");

        List<Tap> taps = List.of(tap, tap2, tap3);

        List<Trip> trips = tapParser.getTrips(taps);

        assertEquals(2, trips.size());
        Trip trip = trips.get(0);

        assertEquals(LocalDateTime.of(2023,1,22,13,00,0), trip.started());
        assertNull(trip.finished());
        assertNull(trip.durationSec());
        assertEquals("Stop1", trip.fromStopId());
        assertNull(trip.toStopId());
        assertEquals(new BigDecimal("7.30"), trip.chargeAmount());
        assertEquals("Company1", trip.companyId());
        assertEquals("Bus37", trip.busID());
        assertEquals("5500005555555559", trip.PAN());
        assertEquals(TripStatus.INCOMPLETED, trip.status());

        trip = trips.get(1);

        assertEquals(LocalDateTime.of(2023,1,22,13,30,0), trip.started());
        assertEquals(LocalDateTime.of(2023,1,22,13,40,0), trip.finished());
        assertEquals(600L, trip.durationSec());
        assertEquals("Stop3", trip.fromStopId());
        assertEquals("Stop3", trip.toStopId());
        assertEquals(BigDecimal.ZERO, trip.chargeAmount());
        assertEquals("Company1", trip.companyId());
        assertEquals("Bus37", trip.busID());
        assertEquals("5500005555555559", trip.PAN());
        assertEquals(TripStatus.CANCELLED, trip.status());
    }

    @Test
    void INCOMPLETED_last_first_process_the_rest() {
        Tap tap = new Tap("1", FORMATTER.parse("22-01-2023 13:00:00", LocalDateTime::from), TapType.ON, "Stop1", "Company1", "Bus37", "5500005555555559");
        Tap tap2 = new Tap("2", FORMATTER.parse("22-01-2023 13:30:00", LocalDateTime::from), TapType.ON, "Stop3", "Company1", "Bus37", "5500005555555559");
        Tap tap3 = new Tap("3", FORMATTER.parse("22-01-2023 13:40:00", LocalDateTime::from), TapType.OFF, "Stop3", "Company1", "Bus37", "5500005555555559");

        List<Tap> taps = List.of(tap, tap2, tap3);

        List<Trip> trips = tapParser.getTrips(taps);

        assertEquals(2, trips.size());
        Trip trip = trips.get(0);

        assertEquals(LocalDateTime.of(2023,1,22,13,00,0), trip.started());
        assertNull(trip.finished());
        assertNull(trip.durationSec());
        assertEquals("Stop1", trip.fromStopId());
        assertNull(trip.toStopId());
        assertEquals(new BigDecimal("7.30"), trip.chargeAmount());
        assertEquals("Company1", trip.companyId());
        assertEquals("Bus37", trip.busID());
        assertEquals("5500005555555559", trip.PAN());
        assertEquals(TripStatus.INCOMPLETED, trip.status());

        trip = trips.get(1);

        assertEquals(LocalDateTime.of(2023,1,22,13,30,0), trip.started());
        assertEquals(LocalDateTime.of(2023,1,22,13,40,0), trip.finished());
        assertEquals(600L, trip.durationSec());
        assertEquals("Stop3", trip.fromStopId());
        assertEquals("Stop3", trip.toStopId());
        assertEquals(BigDecimal.ZERO, trip.chargeAmount());
        assertEquals("Company1", trip.companyId());
        assertEquals("Bus37", trip.busID());
        assertEquals("5500005555555559", trip.PAN());
        assertEquals(TripStatus.CANCELLED, trip.status());
    }
}