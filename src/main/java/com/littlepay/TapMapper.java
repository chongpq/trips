package com.littlepay;

import com.littlepay.domain.Constants;
import com.littlepay.domain.Tap;
import com.littlepay.domain.TapType;

import java.time.LocalDateTime;

public class TapMapper {

    private static final String CSV_SPLIT_REGEX = "\\s*,\\s*";

    public static Tap StringToTapMapper(String line) {
        String[] strings = line.split(TapMapper.CSV_SPLIT_REGEX);
        return new Tap(strings[0], LocalDateTime.parse(strings[1], Constants.FORMATTER), TapType.valueOf(strings[2]),
                strings[3], strings[4], strings[5], strings[6]);
    }
}