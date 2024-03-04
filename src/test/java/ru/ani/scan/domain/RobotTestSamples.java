package ru.ani.scan.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class RobotTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Robot getRobotSample1() {
        return new Robot().id(1L).lots("lots1").period(1L).operationCount(1L).volumeByHour(1L);
    }

    public static Robot getRobotSample2() {
        return new Robot().id(2L).lots("lots2").period(2L).operationCount(2L).volumeByHour(2L);
    }

    public static Robot getRobotRandomSampleGenerator() {
        return new Robot()
            .id(longCount.incrementAndGet())
            .lots(UUID.randomUUID().toString())
            .period(longCount.incrementAndGet())
            .operationCount(longCount.incrementAndGet())
            .volumeByHour(longCount.incrementAndGet());
    }
}
