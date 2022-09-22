package org.example;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class RecordUnitTest {

    @Test
    void testThatTestsCanWork() {
        assertThat(true, equalTo(true));
    }

    @Test
    void testThatRecordInitializesProperlyWhenGivenNullInputs() {
        Record record = new Record(null, 0, null);
    }

    @Test
    void testThatDateIsImmutable() {
        Date originalDate = new Date(System.currentTimeMillis());
        Record record = new Record("182134jo328s", (float) 1.34124, originalDate);
        Date dateToChangeMaliciously = record.getDate();
        dateToChangeMaliciously.setTime(0);
        Date dateToCheck = record.getDate();
        assertThat(dateToCheck, equalTo(originalDate));
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

        assertThat(original != copy, is(true));
        assertThat(original, equalTo(copy));
        assertThat(original.hashCode(), equalTo(copy.hashCode()));
        copy = null;

        for (Record record : recordList) {
            assertThat(original != record, is(true));
            assertThat(original, not(record));
            assertThat(original.hashCode(), not(record.hashCode()));
        }
    }
}