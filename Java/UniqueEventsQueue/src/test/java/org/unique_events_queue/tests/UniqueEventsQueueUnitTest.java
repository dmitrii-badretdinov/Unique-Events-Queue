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


/**
 * Tests the basic non-concurrent functionality of the queue class.
 */
class UniqueEventsQueueUnitTest {
    /*
     * Note: the tests check if the queue is empty by creating an executor that spawns a thread.
     * The company's Code Style Guide says the use of threads in Unit Tests is not appreciated.
     * The executor is used because no way was found to do it without threads.
     */
    static final RecordFactory factory = new RecordFactory(new RecordFactorySettings());
    static final ThreadInfoProvider oneThreadStub = new ThreadInfoProvider(1);

    @Test
    void testThatAddHandlesValidRecord() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        Record record = factory.generateRandomFakeRecord();
        mockQueue.add(record);
    }

    @Test
    void testThatAddHandlesNullInput() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        mockQueue.add(null);
    }

    @Test
    void testThatAddDoesNotAddNullsToQueue() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        List<Record> recordList = new LinkedList<>();
        for (int i = 0; i < 2; i++) {
            recordList.add(factory.generateRandomFakeRecord());
        }
        Iterator<Record> inputIterator = recordList.iterator();
        Iterator<Record> mockOutputIterator = recordList.iterator();

        mockQueue.add(null);
        mockQueue.add(inputIterator.next());
        mockQueue.add(null);
        mockQueue.add(inputIterator.next());
        mockQueue.add(null);

        for(int i = 0; i < 2; i++) {
            assertThat(mockQueue.get()).isEqualTo(mockOutputIterator.next());
        }
    }

    @Test
    void testThatGetReturnsRecord() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        Record mockRecord = factory.generateRandomFakeRecord();
        mockQueue.add(mockRecord);
        assertThat(mockQueue.get()).isEqualTo(mockRecord);
    }

    @Test
    void testThatAddAllInsertsRecordsIntoQueue() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        List<Record> recordList = new LinkedList<>();
        for (int i = 0; i < 3; ++i) {
            recordList.add(factory.generateRandomFakeRecord());
        }
        Iterator<Record> mockIterator = recordList.iterator();

        mockQueue.addAll(recordList);

        for (int i = 0; i < 3; ++i) {
            assertThat(mockQueue.get()).isEqualTo(mockIterator.next());
        }
    }

    @Test
    void testThatAddAllHandlesNullInput() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        mockQueue.addAll(null);
    }

    @Test
    void testThatAddAllHandlesListOfNulls() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        List<Record> list = Lists.newArrayList(null, null, null);

        mockQueue.addAll(list);
    }

    @Test
    void testThatAddAllHandlesListOfNullsAndRecords() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        Record record1 = factory.generateRandomFakeRecord();
        Record record2 = factory.generateRandomFakeRecord();
        List<Record> list = Lists.newArrayList(null, record1, null, record2, null);

        mockQueue.addAll(list);
    }

    @Test
    void testThatQueueAcceptsOnlyProperSizeLimit() {
        assertThatThrownBy(() -> new UniqueEventsQueue(-10, 1, oneThreadStub))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new UniqueEventsQueue(0, 1, oneThreadStub))
            .isInstanceOf(IllegalArgumentException.class);
    }
}