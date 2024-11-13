package com.littlepay;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class MainTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    void test_main() {
        Path resourceDirectory = Paths.get("src","test","resources","taps.csv");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath();
        String[] args = { absolutePath };

        Main.main(args);

        assertEquals("Started, Finished, DurationSecs, FromStopId, ToStopId, ChargeAmount, CompanyId, BusID, PAN, Status\n" + //
                "22-01-2023 13:00:00, 22-01-2023 13:05:00, 300, Stop1, Stop2, $3.25, Company1, Bus37, 5500005555555559, COMPLETED", outputStreamCaptor.toString().trim());
    }
}