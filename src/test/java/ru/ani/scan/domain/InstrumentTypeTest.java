package ru.ani.scan.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.ani.scan.domain.InstrumentTestSamples.*;
import static ru.ani.scan.domain.InstrumentTypeTestSamples.*;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.ani.scan.web.rest.TestUtil;

class InstrumentTypeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InstrumentType.class);
        InstrumentType instrumentType1 = getInstrumentTypeSample1();
        InstrumentType instrumentType2 = new InstrumentType();
        assertThat(instrumentType1).isNotEqualTo(instrumentType2);

        instrumentType2.setId(instrumentType1.getId());
        assertThat(instrumentType1).isEqualTo(instrumentType2);

        instrumentType2 = getInstrumentTypeSample2();
        assertThat(instrumentType1).isNotEqualTo(instrumentType2);
    }

    @Test
    void instrumentTest() throws Exception {
        InstrumentType instrumentType = getInstrumentTypeRandomSampleGenerator();
        Instrument instrumentBack = getInstrumentRandomSampleGenerator();

        instrumentType.addInstrument(instrumentBack);
        assertThat(instrumentType.getInstruments()).containsOnly(instrumentBack);
        assertThat(instrumentBack.getType()).isEqualTo(instrumentType);

        instrumentType.removeInstrument(instrumentBack);
        assertThat(instrumentType.getInstruments()).doesNotContain(instrumentBack);
        assertThat(instrumentBack.getType()).isNull();

        instrumentType.instruments(new HashSet<>(Set.of(instrumentBack)));
        assertThat(instrumentType.getInstruments()).containsOnly(instrumentBack);
        assertThat(instrumentBack.getType()).isEqualTo(instrumentType);

        instrumentType.setInstruments(new HashSet<>());
        assertThat(instrumentType.getInstruments()).doesNotContain(instrumentBack);
        assertThat(instrumentBack.getType()).isNull();
    }
}
