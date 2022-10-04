package org.unique_events_queue.tests;

import org.junit.jupiter.api.Test;
import org.unique_events_queue.Record;

import static org.assertj.core.api.Assertions.assertThat;

public class RecordFactoryUnitTest {
    /**
     * Tests if the generated record resembles a proper record.
     * Currently, only basic sanity checks are applied.
     */
    @Test
    void testThatGeneratedRecordMeetsBasicExpectations() {
        RecordFactory factory = new RecordFactory(new RecordFactorySettings());
        Record mockRecord = factory.generateRandomFakeRecord();

        assertThat(mockRecord.getDate().getTime()).isGreaterThan(0);
        assertThat(mockRecord.getValue()).isGreaterThanOrEqualTo(0);
        assertThat(mockRecord.getId().length()).isGreaterThan(0);
    }
}
