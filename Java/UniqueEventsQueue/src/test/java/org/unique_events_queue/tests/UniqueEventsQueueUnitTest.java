package org.unique_events_queue.tests;

import org.junit.jupiter.api.Test;
import org.testng.collections.Lists;
import org.unique_events_queue.Record;
import org.unique_events_queue.ThreadInfoProvider;
import org.unique_events_queue.UniqueEventsQueue;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class UniqueEventsQueueUnitTest {
    static final RecordFactory factory = new RecordFactory(new RecordFactorySettings());
    static final ThreadInfoProvider provider = new ThreadInfoProvider(1);

    @Test
    void testThatAddHandlesValidRecord() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        Record record = factory.generateRandomTestRecord();
        queue.add(record);
    }

    @Test
    void testThatAddHandlesNullInput() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        queue.add(null);
    }

    @Test
    void testThatAddDoesNotAddNullsToQueue() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        List<Record> recordList = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            recordList.add(factory.generateRandomTestRecord());
        }
        Iterator<Record> inputIterator = recordList.iterator();
        Iterator<Record> outputIterator = recordList.iterator();

        queue.add(null);
        queue.add(inputIterator.next());
        queue.add(null);
        queue.add(inputIterator.next());
        queue.add(null);

        for(int i = 0; i < 2; i++) {
            assertThat(queue.get()).isEqualTo(outputIterator.next());
        }
    }

    @Test
    void testThatGetReturnsRecord() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        Record record = factory.generateRandomTestRecord();
        queue.add(record);
        Object result = queue.get();
        assertThat(result).isEqualTo(record);
    }

    @Test
    void testThatAddAllInsertsRecordsIntoQueue() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        List<Record> recordList = new LinkedList<>();
        for (int i = 0; i < 3; ++i) {
            recordList.add(factory.generateRandomTestRecord());
        }
        Iterator<Record> iterator = recordList.iterator();

        queue.addAll(recordList);

        for (int i = 0; i < 3; ++i) {
            assertThat(queue.get()).isEqualTo(iterator.next());
        }
    }

    @Test
    void testThatAddAllHandlesNullInput() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        queue.addAll(null);
    }

    @Test
    void testThatAddAllHandlesListOfNulls() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        List<Record> list = Lists.newArrayList(null, null, null);

        queue.addAll(list);
    }

    @Test
    void testThatAddAllHandlesListOfNullsAndRecords() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        Record record1 = factory.generateRandomTestRecord();
        Record record2 = factory.generateRandomTestRecord();
        List<Record> list = Lists.newArrayList(null, record1, null, record2, null);

        queue.addAll(list);
    }

    @Test
    void testThatQueueAcceptsOnlyProperSizeLimit() {
        assertThatThrownBy(() -> new UniqueEventsQueue(-10, 1, provider))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new UniqueEventsQueue(0, 1, provider))
            .isInstanceOf(IllegalArgumentException.class);
    }
}