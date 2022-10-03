package org.unique_events_queue.tests;

import org.junit.jupiter.api.Test;
import org.unique_events_queue.Record;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RecordUnitTest {
    @Test
    void testThatRecordInstantiatesWithNullInputs() {
        new Record(null, 0, null);
    }

    @Test
    void testThatRecordInstantiatesWithAnotherRecord() {
        String randomID = "K4s1o3a723";
        float randomValue = (float) 1.1923;
        Date randomDate = new Date(1278346);
        Record record = new Record(randomID, randomValue, randomDate);

        Record mockRecord = new Record(record);

        assertThat(mockRecord.getId()).isEqualTo(randomID);
        assertThat(mockRecord.getValue()).isEqualTo(randomValue);
        assertThat(mockRecord.getId()).isEqualTo(randomID);
    }

    @Test
    void testThatDateIsImmutable() {
        Date mockRandomDate = new Date(128734628);
        Record record = new Record("test", 0, mockRandomDate);
        Date dateToChangeMaliciously = record.getDate();
        dateToChangeMaliciously.setTime(0);
        Date mockDate = record.getDate();
        assertThat(mockDate).isEqualTo(mockRandomDate);
    }

    @Test
    void testThatHashcodeAndEqualsAreProperlyChanged() {
        Date date = new Date(System.currentTimeMillis());
        Record original = new Record("first", (float) 1.2342, date);
        Record copy = new Record("first", (float) 1.2342, date);

        Record otherId = new Record("second", (float) 1.2342, date);
        Record otherAmount = new Record("first", (float) 1.212, date);
        Record otherTime = new Record("first", (float) 1.2342, new Date(System.currentTimeMillis() + 1));
        List<Record> recordList = Arrays.asList(otherId, otherTime, otherAmount);

        assertThat(original).isNotSameAs(copy);
        assertThat(original).isEqualTo(copy);
        assertThat(original.hashCode()).isEqualTo(copy.hashCode());
        /* The warning about not using copy assignment is suppressed because the nullification prevents the misuse of
         * copy in the loop-check below.
         */
        //noinspection UnusedAssignment
        copy = null;

        for (Record record : recordList) {
            assertThat(original).isNotSameAs(record);
            assertThat(original).isNotEqualTo(record);
            assertThat(original.hashCode()).isNotEqualTo(record.hashCode());
        }
    }
}