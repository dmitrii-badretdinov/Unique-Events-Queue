package org.unique_events_queue.tests;

import org.junit.jupiter.api.Test;
import org.unique_events_queue.Record;
import org.unique_events_queue.ThreadInfoProvider;
import org.unique_events_queue.UniqueEventsQueue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

public class UniqueEventsQueueConcurrencyTest {
    /*
     * The same executor is not used for all tests because the tests usually put tasks that
     * are expected to not terminate by themselves and should be cancelled manually.
     * The alternative is that each test creates their own executor.
     * The pros and cons of these approaches are as follows:
     * The Same Executor approach removes the need to initiate an executor for every test, but
     * if we forget to cancel the unending task from it even once, all other tests can give false results.
     * The alternative approach needs to specify an executor for every test, but even if we forget to cancel
     * the unending task, it only creates a memory leak for the duration of the test, but does not affect the results
     * of other tests.
     * The alternative approach makes the tests more reliable, therefore it was chosen.
     */
    static final RecordFactory factory = new RecordFactory(new RecordFactorySettings());
    static final ThreadInfoProvider oneThreadStub = new ThreadInfoProvider(1);

    @Test
    void testThatAddAllNotifiesAllWaitingThreads() {
        // Arrange
        int numberOfThreads = 50;
        UniqueEventsQueue queue = new UniqueEventsQueue(new ThreadInfoProvider(numberOfThreads));
        Runnable runnableTask = queue::get;

        ThreadPoolExecutor mockThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        mockThreadPoolExecutor.setCorePoolSize(0);
        mockThreadPoolExecutor.setMaximumPoolSize(numberOfThreads);

        List<Record> recordList = new LinkedList<>();
        for(int i = 0; i < numberOfThreads; i++) {
            mockThreadPoolExecutor.submit(runnableTask);
            recordList.add(factory.generateRandomFakeRecord());
        }

        // Act
        queue.addAll(recordList);

        // Assert
        mockThreadPoolExecutor.shutdown();
        try {
            if(!mockThreadPoolExecutor.awaitTermination(50, TimeUnit.MILLISECONDS)) {
                fail(QueueErrorMessages.EXECUTOR_TIMEOUT.getMessage());
            }
        } catch (InterruptedException e) {
            fail(QueueErrorMessages.INTERRUPTED_FROM_OUTSIDE.getMessage());
        }

        // Finalize: shutdown executor
        mockThreadPoolExecutor.shutdownNow();
    }

    @Test
    void testThatGetThrowsNoExceptionIfQueueBecomesEmpty() {
        // Arrange
        UniqueEventsQueue queue = new UniqueEventsQueue();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Record> callable = queue::get;
        queue.add(factory.generateRandomFakeRecord());

        // Act
        queue.get();
        Future<Record> mockFuture = executor.submit(callable);

        // Assert
        assertThatThrownBy(() -> mockFuture.get(5, TimeUnit.MILLISECONDS)).isInstanceOf(TimeoutException.class);

        // Finalize: clean executor
        executor.shutdownNow();
    }

    @Test
    void testThatGetThrowsNoExceptionIfQueueIsAlwaysEmpty() {
        // Arrange
        UniqueEventsQueue queue = new UniqueEventsQueue();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Record> callable = queue::get;

        // Act
        Future<Record> mockFuture = executor.submit(callable);

        // Assert
        assertThatThrownBy(() -> mockFuture.get(5, TimeUnit.MILLISECONDS)).isInstanceOf(TimeoutException.class);

        // Finalize: shutdown executor
        executor.shutdownNow();
    }

    @Test
    void testThatQueueTrimsIfQueueLimit1AndTrimInterval1() {
        // Arrange
        UniqueEventsQueue queue = new UniqueEventsQueue(1, 1,
            oneThreadStub);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Record> callable = queue::get;
        for(int i = 0; i < 10; i++) {
            queue.add(factory.generateRandomFakeRecord());
        }

        // Act
        queue.get();
        Future<Record> mockFuture = executor.submit(callable);

        // Assert
        assertThatThrownBy(() -> mockFuture.get(5, TimeUnit.MILLISECONDS)).isInstanceOf(TimeoutException.class);

        // Finalize: shutdown executor
        executor.shutdownNow();
    }

    @Test void testThatQueueTrimsIfQueueLimit2AndTrimInterval1() {
        // Arrange
        UniqueEventsQueue queue = new UniqueEventsQueue(2, 1,
            oneThreadStub);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Record> callable = queue::get;
        for(int i = 0; i < 10; i++) {
            queue.add(factory.generateRandomFakeRecord());
        }

        // Act
        queue.get();
        queue.get();
        Future<Record> mockFuture = executor.submit(callable);

        // Assert
        assertThatThrownBy(() -> mockFuture.get(5, TimeUnit.MILLISECONDS)).isInstanceOf(TimeoutException.class);

        // Finalize: shutdown executor
        executor.shutdownNow();
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
        assertThat(QueueTestUtilities.queueIsEmpty(mockQueue)).isEqualTo(true);
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
        assertThat(QueueTestUtilities.queueIsEmpty(mockQueue)).isEqualTo(true);
    }
}
