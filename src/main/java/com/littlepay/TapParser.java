package com.littlepay;

import com.littlepay.domain.Tap;
import com.littlepay.domain.TapType;
import com.littlepay.domain.Trip;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class TapParser {

    static final Comparator<Tap> TAP_COMPARATOR = Comparator.comparing(Tap::dateTimeUTC)
            .thenComparing(Tap::stopId)
            .thenComparing(Tap::tapType);
    
    private final TripCalculator tripCalculator;

    public TapParser(TripCalculator tripCalculator) {
        this.tripCalculator = tripCalculator;
    }

    public List<Trip> getTrips(List<Tap> taps) {
        List<Trip> result = new ArrayList<>();
        
        Map<String, LinkedList<Tap>> customerTapsMap = createCustomerTapsMap(taps);

        for (String customer : customerTapsMap.keySet()) {
            LinkedList<Tap> customerTaps = customerTapsMap.get(customer);
            result.addAll(createTripsForCustomerTaps(customerTaps));
        }

        result.sort(Comparator.comparing(Trip::started));

        return result;
    }

    private List<Trip> createTripsForCustomerTaps(LinkedList<Tap> customerTaps) {
        List<Trip> result = new ArrayList<>();
        // the TAP_COMPARATOR ensures it is sorted by the non-customer values. The main value
        // being DateTimeUTC
        customerTaps.sort(TAP_COMPARATOR);

        Tap tap = customerTaps.pollFirst();
        while (tap != null) {
            final Tap tapFinal = tap;

            calculatePairing(customerTaps, () -> result.add(tripCalculator.createIncompletedTrip(tapFinal)),
                    (t) -> result.add(tripCalculator.createTrip(tapFinal, t)));

            tap = customerTaps.pollFirst();
        }

        return result;
    }

    private void calculatePairing(LinkedList<Tap> taps, Supplier<Boolean> solitary, Function<Tap, Boolean> pairing) {
        Tap tap = taps.peekFirst();
        switch (tap) {
            case Tap t when t.tapType() == TapType.OFF -> {
                taps.removeFirst();
                pairing.apply(tap);
            }
            case null, default -> solitary.get();
        }
    }

    private Map<String, LinkedList<Tap>> createCustomerTapsMap(List<Tap> taps) {
        Map<String, LinkedList<Tap>> customerTaps = new HashMap<>();

        for (Tap tap : taps) {
            String key = String.format("%s-%s-%s", tap.PAN(), tap.busID(), tap.companyId());
            if (!customerTaps.containsKey(key)) {
                customerTaps.put(key, new LinkedList<>());
            }
            customerTaps.get(key).add(tap);
        }
        
        return customerTaps;
    }

}
