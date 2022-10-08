package org.unique_events_queue;

import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests various concurrent-related cases of the UniqueEventsQueue class.
 */

public class UniqueEventsQueueConcurrencyTest {
    /*
     * The same executor is not used for all tests because the tests usually put tasks that
     * are expected to not terminate by themselves and should be cancelled manually.
     * The alternative is that each test creates their own executor.
     *
     * The pros and cons of these approaches are as follows:
     *
     * The Same Executor approach removes the need to initiate an executor for every test, but
     * if we forget to cancel the unending task from it even once, all other tests can give false results.
     * 
     * The alternative approach needs to specify an executor for every test, but even if we forget to cancel
     * the unending task, it only creates a memory leak for the duration of the test. It does not affect the results
     * of other tests. It makes the tests more reliable, therefore it was chosen.
     *
     * We don't use just threads instead of Executors because without Executors we won't have the much-needed
     * functionality of Future.
     */
    static final RecordFactory factory = new RecordFactory(new RecordFactorySettings());

    @Test
    void testThatAddNotifiesWaitingThread() {
        // Arrange
        UniqueEventsQueue queue =  new UniqueEventsQueue();
        Callable<Record> callable = queue::get;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Record> future = executor.submit(callable);

        // Act
        queue.add(factory.generateRandomFakeRecord());

        // Assert
        assertThat(QueueTestUtilities.getFutureAndHandleExceptions(future)).isInstanceOf(Record.class);
    }

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
}
