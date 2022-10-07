package org.unique_events_queue;

import org.junit.jupiter.api.Test;
import org.testng.collections.Lists;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


/**
 * Tests the basic non-concurrent functionality of the queue class.
 */
class UniqueEventsQueueUnitTest {
    static final RecordFactory factory = new RecordFactory(new RecordFactorySettings());
    static final ThreadInfoProvider oneThreadStub = new ThreadInfoProvider(1);

    // region add()
    @Test
    void testThatAddHandlesValidRecord() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        Record record = factory.generateRandomFakeRecord();
        mockQueue.add(record);

        assertThat(mockQueue.isEmpty()).isEqualTo(false);
    }

    @Test
    void testThatAddHandlesNullInput() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        mockQueue.add(null);

        assertThat(mockQueue.isEmpty()).isEqualTo(true);
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
    // endregion

    // region addAll()
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

        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    @Test
    void testThatAddAllHandlesListOfNulls() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        List<Record> list = Lists.newArrayList(null, null, null);

        mockQueue.addAll(list);

        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    @Test
    void testThatAddAllHandlesListOfNullsAndRecords() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        Record record1 = factory.generateRandomFakeRecord();
        Record record2 = factory.generateRandomFakeRecord();
        List<Record> list = Lists.newArrayList(null, record1, null, record2, null);

        mockQueue.addAll(list);

        assertThat(mockQueue.get()).isEqualTo(record1);
        assertThat(mockQueue.get()).isEqualTo(record2);
    }
    // endregion

    // region Trimming
    @Test
    void testThatQueueTrimsIfQueueLimit1AndTrimInterval1() {
        // Arrange
        UniqueEventsQueue queue = new UniqueEventsQueue(1, 1,
            oneThreadStub);
        for(int i = 0; i < 10; i++) {
            queue.add(factory.generateRandomFakeRecord());
        }

        // Act
        queue.get();

        // Assert
        assertThat(queue.isEmpty()).isEqualTo(true);
    }

    @Test void testThatQueueTrimsIfQueueLimit2AndTrimInterval1() {
        // Arrange
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(2, 1,
            oneThreadStub);
        for(int i = 0; i < 10; i++) {
            mockQueue.add(factory.generateRandomFakeRecord());
        }

        // Act
        mockQueue.get();
        mockQueue.get();

        // Assert
        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    @Test
    void testThatQueueTrimsForAdd() {
        // Arrange
        long numberOfRecords = 50;
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1, numberOfRecords,
            oneThreadStub);
        for(int i = 0; i < numberOfRecords; i++) {
            mockQueue.add(factory.generateRandomFakeRecord());
        }

        // Act
        mockQueue.get();

        // Assert
        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    @Test
    void testThatQueueTrimsForAddAll() {
        long numberOfRecords = 50;
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1, numberOfRecords, oneThreadStub);
        List<Record> firstRecordList = new LinkedList<>();
        List<Record> secondRecordList = new LinkedList<>();

        for(int i = 0; i < numberOfRecords; i++) {
            firstRecordList.add(factory.generateRandomFakeRecord());
            secondRecordList.add(factory.generateRandomFakeRecord());
        }

        mockQueue.addAll(firstRecordList);
        mockQueue.addAll(secondRecordList);
        Iterator<Record> mockIterator = secondRecordList.iterator();
        for(int i = 0; i < secondRecordList.size(); i++) {
            assertThat(mockQueue.get()).isEqualTo(mockIterator.next());
        }
    }

    @Test
    void testThatQueueDoesNotTrimWhenTrimIntervalIsNotReachedForAdd() {
        long numberOfRecords = 50;
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1,
            numberOfRecords + 1, oneThreadStub);

        for(int i = 0; i < numberOfRecords; i++) {
            mockQueue.add(factory.generateRandomFakeRecord());
        }

        QueueTestUtilities.drainRecords(mockQueue, numberOfRecords);
        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    @Test
    void testThatQueueDoesNotTrimWhenTrimIntervalIsNotReachedForAddAll() {
        long numberOfRecords = 50;
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1,
            numberOfRecords + 1, oneThreadStub);
        List<Record> recordList = new LinkedList<>();

        for(int i = 0; i < numberOfRecords; i++) {
            recordList.add(factory.generateRandomFakeRecord());
        }

        mockQueue.addAll(recordList);
        QueueTestUtilities.drainRecords(mockQueue, numberOfRecords);
        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }
    // endregion

    // region Other tests
    @Test
    void testThatQueueIsEmpty() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        mockQueue.add(factory.generateRandomFakeRecord());

        assertThat(mockQueue.isEmpty()).isEqualTo(false);
    }

    @Test
    void testThatGetReturnsRecord() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        Record mockRecord = factory.generateRandomFakeRecord();
        mockQueue.add(mockRecord);
        assertThat(mockQueue.get()).isEqualTo(mockRecord);
    }

    @Test
    void testThatQueueAcceptsOnlyProperSizeLimit() {
        assertThatThrownBy(() -> new UniqueEventsQueue(-10, 1, oneThreadStub))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new UniqueEventsQueue(0, 1, oneThreadStub))
                .isInstanceOf(IllegalArgumentException.class);
    }
    // endregion
}