package org.example;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RecordFactoryUnitTest {

    @Test
    void testThatTestsCanWork() {
        assertThat(true, equalTo(true));
    }

    @Test
    void testThatTheGeneratedRecordResemblesAProperRecord() {
        RecordFactory factory = new RecordFactory(42);
        Record record = factory.generateRandomTestRecord();

        assertThat(record.getDate().getTime() > 0, equalTo(true));
        assertThat(record.getValue() >= 0, equalTo(true));
        assertThat(record.getId().length() > 0, equalTo(true));
    }
}
