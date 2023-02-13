package org.unique_events_queue;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Date;
import org.junit.jupiter.api.Test;

public class RecordUnitTest {
    /**
     * Tests the Record class against the attack on its Date field.
     * The attacker can retrieve date and try to change its value because Date is mutable.
     * To prevent that, a defensive copy is expected from the Record class.
     */
    @Test
    void testThatDateIsImmutable() {
        Date mockRandomDate = new Date(128734628);
        Record record = new Record("test", 0, mockRandomDate);
        Date dateToChangeMaliciously = record.getDate();
        dateToChangeMaliciously.setTime(0);
        Date mockDate = record.getDate();
        assertThat(mockDate).isEqualTo(mockRandomDate);
    }

    // region Initialization tests
    @Test
    void testThatRecordInstantiatesWithNullInputs() {
        new Record(null, 0, null);
    }

    @Test
    void testThatRecordInstantiatesWithAnotherRecord() {
        /* The values were chosen randomly and hold to hidden meaning. */
        String mockId = "K4s1o3a723";
        float mockValue = (float) 1.1923;
        Date mockDate = new Date(1278346);
        Record record = new Record(mockId, mockValue, mockDate);

        Record mockRecord = new Record(record);

        assertThat(mockRecord.getId()).isEqualTo(mockId);
        assertThat(mockRecord.getValue()).isEqualTo(mockValue);
        assertThat(mockRecord.getId()).isEqualTo(mockId);
    }
    // endregion

    // region Fields equality and hashCode
    @Test
    void testThatIdenticalRecordIsDetected() {
        Record mockOriginal = new Record("0", 0, new Date(0));
        Record mockCopy = new Record("0", 0, new Date(0));
        assertThat(mockOriginal).isNotSameAs(mockCopy);
        assertThat(mockOriginal).isEqualTo(mockCopy);
        assertThat(mockOriginal.hashCode()).isEqualTo(mockCopy.hashCode());
    }

    @Test
    void testThatDifferentIdIsDetected() {
        Record mockOriginal = new Record("1", 0, new Date(0));
        Record mockDifferentId = new Record("2", 0, new Date(0));
        assertThat(mockOriginal).isNotSameAs(mockDifferentId);
        assertThat(mockOriginal).isNotEqualTo(mockDifferentId);
        assertThat(mockOriginal.hashCode()).isNotEqualTo(mockDifferentId.hashCode());
    }

    @Test
    void testThatDifferentValueIsDetected() {
        Record mockOriginal = new Record("0", 0, new Date(0));
        Record mockDifferentValue = new Record("0", 1, new Date(0));
        assertThat(mockOriginal).isNotSameAs(mockDifferentValue);
        assertThat(mockOriginal).isNotEqualTo(mockDifferentValue);
        assertThat(mockOriginal.hashCode()).isNotEqualTo(mockDifferentValue.hashCode());
    }

    @Test
    void testThatDifferentDateIsDetected() {
        Record mockOriginal = new Record("0", 0, new Date(0));
        Record mockDifferentDate = new Record("0", 0, new Date(1));
        assertThat(mockOriginal).isNotSameAs(mockDifferentDate);
        assertThat(mockOriginal).isNotEqualTo(mockDifferentDate);
        assertThat(mockOriginal.hashCode()).isNotEqualTo(mockDifferentDate.hashCode());
    }
    // endregion
}
