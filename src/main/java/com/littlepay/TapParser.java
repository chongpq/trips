package com.littlepay;

import com.littlepay.domain.Tap;
import com.littlepay.domain.TapType;
import com.littlepay.domain.Trip;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public class TapParser {

    private static final Comparator<Tap> TAP_COMPARATOR = (o1, o2) -> {
        if (!o1.dateTimeUTC().isEqual(o2.dateTimeUTC())) {
            return o1.dateTimeUTC().compareTo(o2.dateTimeUTC());
        } else if (!o1.stopId().equals(o2.stopId())) {
            return o1.stopId().compareTo(o2.stopId());
        } else {
            return o1.tapType().compareTo(o2.tapType());
        }
    };
    
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
                    (t) -> result.add(tripCalculator.calculate(tapFinal, t)));

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
