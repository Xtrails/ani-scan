package ru.ani.scan.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InstrumentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Instrument getInstrumentSample1() {
        return new Instrument().id(1L).secCode("secCode1");
    }

    public static Instrument getInstrumentSample2() {
        return new Instrument().id(2L).secCode("secCode2");
    }

    public static Instrument getInstrumentRandomSampleGenerator() {
        return new Instrument().id(longCount.incrementAndGet()).secCode(UUID.randomUUID().toString());
    }
}
