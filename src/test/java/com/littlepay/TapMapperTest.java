package com.littlepay;

import com.littlepay.domain.Tap;
import com.littlepay.domain.TapType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TapMapperTest {

    @Test
    void stringToTapMapper() {
        String input = "1, 22-01-2018 13:00:00, ON, Stop1, Company1, Bus37, 5500005555555559";

        Tap tap = TapMapper.StringToTapMapper(input);

        assertEquals("1", tap.ID());
        assertEquals(LocalDateTime.of(2018,1,22,13,0,0), tap.dateTimeUTC());
        assertEquals(TapType.ON, tap.tapType());
        assertEquals("Stop1", tap.stopId());
        assertEquals("Company1", tap.companyId());
        assertEquals("Bus37", tap.busID());
        assertEquals("5500005555555559", tap.PAN());
    }
}