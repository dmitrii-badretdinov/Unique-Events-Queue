package org.unique_events_queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.testng.collections.Lists;

/**
 * Tests the basic non-concurrent functionality of the queue class.
 */
class UniqueEventsQueueUnitTest {
    static final RecordFactory factory = new RecordFactory(new RecordFactorySettings());

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

        for (int i = 0; i < 2; i++) {
            assertThat(QueueTestUtilities.getOrThrow(mockQueue)).isEqualTo(mockOutputIterator.next());
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
            assertThat(QueueTestUtilities.getOrThrow(mockQueue)).isEqualTo(mockIterator.next());
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

        assertThat(QueueTestUtilities.getOrThrow(mockQueue)).isEqualTo(record1);
        assertThat(QueueTestUtilities.getOrThrow(mockQueue)).isEqualTo(record2);
    }
    // endregion

    // region add() trimming
    @Test
    void testThatQueueTrimsIfQueueLimit1AndTrimInterval1() {
        // Arrange
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1, 1);
        for (int i = 0; i < 10; i++) {
            mockQueue.add(factory.generateRandomFakeRecord());
        }

        // Act
        QueueTestUtilities.getOrThrow(mockQueue);

        // Assert
        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    @Test
    void testThatQueueTrimsIfQueueLimit2AndTrimInterval1() {
        // Arrange
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(2, 1);
        for (int i = 0; i < 10; i++) {
            mockQueue.add(factory.generateRandomFakeRecord());
        }

        // Act
        QueueTestUtilities.getOrThrow(mockQueue);
        QueueTestUtilities.getOrThrow(mockQueue);

        // Assert
        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    @Test
    void testThatQueueTrimsForAdd() {
        // Arrange
        long numberOfRecords = 50;
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1, numberOfRecords);
        for (int i = 0; i < numberOfRecords; i++) {
            mockQueue.add(factory.generateRandomFakeRecord());
        }

        // Act
        QueueTestUtilities.getOrThrow(mockQueue);

        // Assert
        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    @Test
    void testThatQueueDoesNotTrimWhenTrimIntervalIsNotReachedForAdd() {
        long numberOfRecords = 50;
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1, numberOfRecords + 1);

        for (int i = 0; i < numberOfRecords; i++) {
            mockQueue.add(factory.generateRandomFakeRecord());
        }

        QueueTestUtilities.drainRecords(mockQueue, numberOfRecords);
        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    @Test
    void testThatQueueTrimsWhenLimitIsExceededBy1ForAdd() {
        long numberOfRecords = 50;
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1, numberOfRecords);

        for (int i = 0; i < numberOfRecords; i++) {
            mockQueue.add(factory.generateRandomFakeRecord());
        }

        QueueTestUtilities.getOrThrow(mockQueue);
        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    // endregion

    // region addAll() trimming
    @Test
    void testThatQueueTrimsForAddAll() {
        long numberOfRecords = 50;
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1, numberOfRecords);
        List<Record> firstRecordList = new LinkedList<>();
        List<Record> secondRecordList = new LinkedList<>();

        for (int i = 0; i < numberOfRecords; i++) {
            firstRecordList.add(factory.generateRandomFakeRecord());
            secondRecordList.add(factory.generateRandomFakeRecord());
        }

        mockQueue.addAll(firstRecordList);
        mockQueue.addAll(secondRecordList);
        Iterator<Record> mockIterator = secondRecordList.iterator();
        for (int i = 0; i < secondRecordList.size(); i++) {
            assertThat(QueueTestUtilities.getOrThrow(mockQueue)).isEqualTo(mockIterator.next());
        }
    }

    @Test
    void testThatQueueDoesNotTrimWhenTrimIntervalIsNotReachedForAddAll() {
        // Arrange
        long numberOfRecords = 25;
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1, numberOfRecords * 2 + 1);
        List<Record> recordList1 = new LinkedList<>();
        List<Record> recordList2 = new LinkedList<>();
        for (int i = 0; i < numberOfRecords; i++) {
            recordList1.add(factory.generateRandomFakeRecord());
            recordList2.add(factory.generateRandomFakeRecord());
        }

        // Act
        mockQueue.addAll(recordList1);
        mockQueue.addAll(recordList2);
        QueueTestUtilities.drainRecords(mockQueue, numberOfRecords * 2);

        // Assert
        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    @Test
    void testThatQueueTrimsWhenLimitIsExceededBy1ForAddAll() {
        // Arrange
        long numberOfRecords = 25;
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1, numberOfRecords * 2);
        List<Record> recordList1 = new LinkedList<>();
        List<Record> recordList2 = new LinkedList<>();
        for (int i = 0; i < numberOfRecords; i++) {
            recordList1.add(factory.generateRandomFakeRecord());
            recordList2.add(factory.generateRandomFakeRecord());
        }

        // Act
        mockQueue.addAll(recordList1);
        mockQueue.addAll(recordList2);

        // Assert
        Iterator<Record> iterator = recordList2.iterator();
        for (int i = 0; i < numberOfRecords; ++i) {
            assertThat(QueueTestUtilities.getOrThrow(mockQueue)).isEqualTo(iterator.next());
        }
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
    void testThatGetThrowsNoExceptionIfQueueBecomesEmpty() {
        // Arrange
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        mockQueue.add(factory.generateRandomFakeRecord());

        // Act
        QueueTestUtilities.getOrThrow(mockQueue);

        // Assert
        assertThat(mockQueue.isEmpty()).isEqualTo(true);
    }

    @Test
    void testThatGetReturnsRecord() {
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        Record mockRecord = factory.generateRandomFakeRecord();
        mockQueue.add(mockRecord);
        assertThat(QueueTestUtilities.getOrThrow(mockQueue)).isEqualTo(mockRecord);
    }

    @Test
    void testThatQueueAcceptsOnlyProperSizeLimit() {
        assertThatThrownBy(() -> new UniqueEventsQueue(-10, 1)).isInstanceOf(RuntimeException.class);
        assertThatThrownBy(() -> new UniqueEventsQueue(0, 1)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void testsThatParametrizedGetThrowsException() {
        UniqueEventsQueue queue = new UniqueEventsQueue();

        assertThatThrownBy(() -> queue.get(1, true)).isInstanceOf(RuntimeException.class);
    }
    // endregion
}
