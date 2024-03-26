package ru.ani.scan.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class InstrumentTypeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static InstrumentType getInstrumentTypeSample1() {
        return new InstrumentType().id(1L).name("name1");
    }

    public static InstrumentType getInstrumentTypeSample2() {
        return new InstrumentType().id(2L).name("name2");
    }

    public static InstrumentType getInstrumentTypeRandomSampleGenerator() {
        return new InstrumentType().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString());
    }
}
