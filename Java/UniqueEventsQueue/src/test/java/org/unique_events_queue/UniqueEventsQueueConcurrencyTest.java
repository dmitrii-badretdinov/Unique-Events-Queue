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

    /**
     * When it is possible to check that addAll() notifies only the equivalent of the list length when the list
     * is smaller than the number of threads, the opposite is hard to check because we would need to check that
     * addAll() doesn't send more notifies than necessary.
     * As it is right now, there is no test for that.
     */
    @Test
    void testThatAddAllNotifiesEquivalentOfAddedListLengthIfItIsBelowThreadNumber() {
        // Arrange
        int numberOfThreads = 50;
        int listLength = 25;
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        CountDownLatchSwitch mockLatch = new CountDownLatchSwitch(CountDownPosition.WENT_TO_WAIT,
            2 * numberOfThreads - listLength);
        Runnable runnableTask = () -> mockQueue.get(5000, true, mockLatch);
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Record> recordList = new LinkedList<>();
        for (int i = 0; i < numberOfThreads; ++i) {
            executor.submit(runnableTask);
        }
        for (int i = 0; i < listLength; ++i) {
            recordList.add(factory.generateRandomFakeRecord());
        }

        // Act
        mockQueue.addAll(recordList);
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            fail(QueueErrorMessages.INTERRUPTED.getMessage());
        }

        // Assert
        assertThat(mockLatch.getCount()).isGreaterThan(0);
        assertThat(mockQueue.waitingThreadsCount()).isEqualTo(numberOfThreads - listLength);

        // Finalize: shutdown executor
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
    void testThatWaitingGetIsAddedToWaitingThreadsMap() {
        // Arrange
        UniqueEventsQueue mockQueue = new UniqueEventsQueue();
        Runnable runnable = mockQueue::get;
        Thread thread = new Thread(runnable);

        // Act
        thread.start();
        QueueTestUtilities.joinThreadAndHandleTimeout(thread, 1);

        // Assert
        assertThat(mockQueue.waitingThreadsCount()).isEqualTo(1);

        // Finalize
        thread.interrupt();
    }

    @Test
    void testThatMultipleGetsAreAddedToWaitingThreadsMap() {
        // Arrange
        int numberOfWaitingGets = 10;
        UniqueEventsQueue queue = new UniqueEventsQueue();
        ExecutorService executor = Executors.newCachedThreadPool();
        Runnable runnable = queue::get;

        // Act
        for (int i = 0; i < numberOfWaitingGets; ++i) {
            executor.submit(runnable);
        }
        QueueTestUtilities.sleepAndHandleInterruption(Thread.currentThread(), 1);

        // Assert
        assertThat(queue.waitingThreadsCount()).isEqualTo(numberOfWaitingGets);

        // Finalize
        executor.shutdownNow();
    }

    @Test
    void testThatFinishedGetIsSubtractedFromWaitingThreadsMap() {
        // Arrange
        int numberOfWaitingGets = 10;
        UniqueEventsQueue queue = new UniqueEventsQueue();
        ExecutorService executor = Executors.newFixedThreadPool(numberOfWaitingGets);
        Runnable runnable = queue::get;
        for (int i = 0; i < numberOfWaitingGets; ++i) {
            executor.submit(runnable);
        }
        QueueTestUtilities.sleepAndHandleInterruption(Thread.currentThread(), 1);
        assertThat(queue.waitingThreadsCount()).isEqualTo(numberOfWaitingGets);

        // Act
        queue.add(factory.generateRandomFakeRecord());
        QueueTestUtilities.sleepAndHandleInterruption(Thread.currentThread(), 1);

        // Assert
        assertThat(queue.waitingThreadsCount()).isEqualTo(numberOfWaitingGets - 1);

        // Finalize
        executor.shutdownNow();
    }
}
