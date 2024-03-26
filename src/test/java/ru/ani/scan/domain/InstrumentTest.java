package ru.ani.scan.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.ani.scan.domain.InstrumentTestSamples.*;
import static ru.ani.scan.domain.InstrumentTypeTestSamples.*;
import static ru.ani.scan.domain.RobotTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.ani.scan.web.rest.TestUtil;

class InstrumentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Instrument.class);
        Instrument instrument1 = getInstrumentSample1();
        Instrument instrument2 = new Instrument();
        assertThat(instrument1).isNotEqualTo(instrument2);

        instrument2.setId(instrument1.getId());
        assertThat(instrument1).isEqualTo(instrument2);

        instrument2 = getInstrumentSample2();
        assertThat(instrument1).isNotEqualTo(instrument2);
    }

    @Test
    void robotTest() throws Exception {
        Instrument instrument = getInstrumentRandomSampleGenerator();
        Robot robotBack = getRobotRandomSampleGenerator();

        instrument.addRobot(robotBack);
        assertThat(instrument.getRobots()).containsOnly(robotBack);
        assertThat(robotBack.getInstrument()).isEqualTo(instrument);

        instrument.removeRobot(robotBack);
        assertThat(instrument.getRobots()).doesNotContain(robotBack);
        assertThat(robotBack.getInstrument()).isNull();

        instrument.robots(new HashSet<>(Set.of(robotBack)));
        assertThat(instrument.getRobots()).containsOnly(robotBack);
        assertThat(robotBack.getInstrument()).isEqualTo(instrument);

        instrument.setRobots(new HashSet<>());
        assertThat(instrument.getRobots()).doesNotContain(robotBack);
        assertThat(robotBack.getInstrument()).isNull();
    }

    @Test
    void typeTest() throws Exception {
        Instrument instrument = getInstrumentRandomSampleGenerator();
        InstrumentType instrumentTypeBack = getInstrumentTypeRandomSampleGenerator();

        instrument.setType(instrumentTypeBack);
        assertThat(instrument.getType()).isEqualTo(instrumentTypeBack);

        instrument.type(null);
        assertThat(instrument.getType()).isNull();
    }
}
