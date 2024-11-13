package com.littlepay.domain;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Constants {

    // the STOP_ID_MAP can be read from a config file or another data source
    public static final Map<String, Integer> STOP_ID_MAP = Map.of(
            "Stop1", 0,
            "Stop2", 1,
            "Stop3", 2
    );

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("$#,###.00");

}
