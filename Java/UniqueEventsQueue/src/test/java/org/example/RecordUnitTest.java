package org.example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RecordUnitTest {
    @Test
    void testThatRecordInitializesProperlyWhenGivenNullInputs() {
        new Record(null, 0, null);
    }

    @Test
    void testThatRecordInitializesProperlyWhenGivenAnotherRecord() {
        Record record = new Record(null, 0, null);
        new Record(record);
    }

    @Test
    void testThatDateIsImmutable() {
        Date originalDate = new Date(System.currentTimeMillis());
        Record record = new Record("182134jo328s", (float) 1.34124, originalDate);
        Date dateToChangeMaliciously = record.getDate();
        dateToChangeMaliciously.setTime(0);
        Date dateToCheck = record.getDate();
        assertThat(dateToCheck).isEqualTo(originalDate);
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