package org.unique_events_queue.tests;

import org.junit.jupiter.api.Test;
import org.unique_events_queue.Record;

import static org.assertj.core.api.Assertions.assertThat;

public class RecordFactoryUnitTest {
    @Test
    void testThatTheGeneratedRecordResemblesAProperRecord() {
        RecordFactory factory = new RecordFactory(new RecordFactorySettings());
        Record record = factory.generateRandomTestRecord();

        assertThat(record.getDate().getTime()).isGreaterThan(0);
        assertThat(record.getValue()).isGreaterThanOrEqualTo(0);
        assertThat(record.getId().length()).isGreaterThan(0);
    }
}
