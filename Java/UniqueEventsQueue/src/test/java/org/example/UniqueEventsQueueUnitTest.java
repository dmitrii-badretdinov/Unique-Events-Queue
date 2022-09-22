package org.example;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testng.collections.Lists;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UniqueEventsQueueUnitTest {
    static long factorySeed = 182376523;
    static RecordFactory factory;

    @BeforeAll
    static void executeBeforeAllTests() {
        factory = new RecordFactory(factorySeed);
    }

    @Test
    void testThatTestsCanWork() {
        assertThat(true).isEqualTo(true);
    }

    @Test
    void testThatAddHandlesValidRecord() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        Record record = factory.generateRandomTestRecord();
        queue.add(record);
    }

    @Test
    void testThatAddHandlesNullInput() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        Record record = null;
        queue.add(record);
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
        List<Record> recordList = null;
        queue.addAll(recordList);
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
        try {
            UniqueEventsQueue queue = new UniqueEventsQueue(-10);
        } catch (IllegalArgumentException e) {}

        try {
            UniqueEventsQueue queue = new UniqueEventsQueue(0);
        } catch (IllegalArgumentException e) {}
    }
}