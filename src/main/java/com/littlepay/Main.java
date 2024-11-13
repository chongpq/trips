package com.littlepay;

import com.littlepay.domain.CompletedTripCosts;
import com.littlepay.domain.IncompleteTripCosts;
import com.littlepay.domain.Tap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class Main {

    private static final String OUTPUT_FILE_HEADER = "Started, Finished, DurationSecs, FromStopId, ToStopId,"
            + " ChargeAmount, CompanyId, BusID, PAN, Status";
    private static final String INPUT_FILE_HEADER = "ID, DateTimeUTC, TapType, StopId, CompanyId, BusID, PAN";


    public static void main(String[] args) {
        IncompleteTripCosts incompleteTripCosts = new IncompleteTripCosts();
        CompletedTripCosts completedTripCosts = new CompletedTripCosts();
        TripCalculator tripCalculator = new TripCalculator(incompleteTripCosts, completedTripCosts);
        TapParser tapsParser = new TapParser(tripCalculator);

        try (Stream<String> stream = Files.lines(Paths.get(args[0]))) {
            List<Tap> taps = stream.filter(
                            s -> !s.equals(INPUT_FILE_HEADER))
                    .map(TapMapper::StringToTapMapper)
                    .toList();

            System.out.println(OUTPUT_FILE_HEADER);
            tapsParser.getTrips(taps)
                    .stream()
                    .map(TripMapper::TripToStringMapper)
                    .forEach(System.out::println);
        } catch (IOException e) {
            System.err.printf("Error reading file '%s'%n", args[0]);
        }
    }
}