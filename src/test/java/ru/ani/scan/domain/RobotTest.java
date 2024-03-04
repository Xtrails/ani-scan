package ru.ani.scan.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.ani.scan.domain.InstrumentTestSamples.*;
import static ru.ani.scan.domain.RobotTestSamples.*;

import org.junit.jupiter.api.Test;
import ru.ani.scan.web.rest.TestUtil;

class RobotTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Robot.class);
        Robot robot1 = getRobotSample1();
        Robot robot2 = new Robot();
        assertThat(robot1).isNotEqualTo(robot2);

        robot2.setId(robot1.getId());
        assertThat(robot1).isEqualTo(robot2);

        robot2 = getRobotSample2();
        assertThat(robot1).isNotEqualTo(robot2);
    }

    @Test
    void instrumentTest() throws Exception {
        Robot robot = getRobotRandomSampleGenerator();
        Instrument instrumentBack = getInstrumentRandomSampleGenerator();

        robot.setInstrument(instrumentBack);
        assertThat(robot.getInstrument()).isEqualTo(instrumentBack);

        robot.instrument(null);
        assertThat(robot.getInstrument()).isNull();
    }
}
