package org.unique_events_queue.tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.unique_events_queue.Record;
import org.unique_events_queue.ThreadInfoProvider;
import org.unique_events_queue.UniqueEventsQueue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class UniqueEventsQueueConcurrencyTest {
    static RecordFactory factory;

    @BeforeAll
    static void executeBeforeAllTests() {
        factory = new RecordFactory(new RecordFactorySettings());
    }

    @Test
    void testThatAddAllNotifiesAllWaitingThreads() {
        UniqueEventsQueue queue = new UniqueEventsQueue(new ThreadInfoProvider(50));
        Runnable runnableTask = queue::get;
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        threadPoolExecutor.setCorePoolSize(0);
        threadPoolExecutor.setMaximumPoolSize(50);
        List<Record> recordList = new LinkedList<>();

        for(int i = 0; i < 50; i++) {
            threadPoolExecutor.submit(runnableTask);
            recordList.add(factory.generateRandomTestRecord());
        }

        queue.addAll(recordList);
        threadPoolExecutor.shutdown();

        try {
            if(!threadPoolExecutor.awaitTermination(50, TimeUnit.MILLISECONDS)) {
                fail(QueueErrorMessages.EXECUTOR_TIMEOUT.getMessage());
            }
        } catch (InterruptedException e) {
            fail(QueueErrorMessages.INTERRUPTED_FROM_OUTSIDE.getMessage());
        }
    }

    @Test
    void testThatGetThrowsNoExceptionIfQueueBecomesEmpty() {
        UniqueEventsQueue uniqueEventsQueue = new UniqueEventsQueue();
        Callable<Record> callable = uniqueEventsQueue::get;
        Runnable runnable = uniqueEventsQueue::get;
        ExecutorService executorThread1 = Executors.newSingleThreadExecutor();
        Thread thread2 = new Thread(runnable);
        Record result = null;
        Record record = factory.generateRandomTestRecord();

        uniqueEventsQueue.add(record);
        
        Future<Record> future = executorThread1.submit(callable);

        try {
            result = future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            fail(QueueErrorMessages.INTERRUPTED_FROM_OUTSIDE.getMessage());
        } catch (TimeoutException e) {
            fail(QueueErrorMessages.THREAD_TIMEOUT.getMessage());
        }

        thread2.start();

        try {
            thread2.join(10);
        } catch (InterruptedException e) {
            fail(QueueErrorMessages.INTERRUPTED_FROM_OUTSIDE.getMessage());
        }

        assertThat(result).isEqualTo(record);
        thread2.interrupt();
    }

    @Test
    void testThatGetThrowsNoExceptionIfQueueIsAlwaysEmpty() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        Runnable runnable = queue::get;
        Thread thread = new Thread(runnable);

        thread.start();
        try {
            thread.join(10);
        } catch (InterruptedException e) {
            fail(QueueErrorMessages.INTERRUPTED_FROM_OUTSIDE.getMessage());
        }
        thread.interrupt();
    }

    @Test
    void testThatQueueTrimsItselfToSpecifiedSize() {
        UniqueEventsQueue queueWithLimit1 = new UniqueEventsQueue(1, 1,
            new ThreadInfoProvider(1));
        Runnable runnable = queueWithLimit1::get;
        Thread thread1 = new Thread(runnable);

        for(int i = 0; i < 10; i++) {
            queueWithLimit1.add(factory.generateRandomTestRecord());
        }

        queueWithLimit1.get();
        thread1.start();
        try {
            thread1.join(10);
        } catch (InterruptedException e) {
            fail(QueueErrorMessages.INTERRUPTED_FROM_OUTSIDE.getMessage());
        }
        thread1.interrupt();

        UniqueEventsQueue queueWithLimit2 = new UniqueEventsQueue(2, 1,
            new ThreadInfoProvider(1));
        Thread thread2 = new Thread(runnable);

        for(int i = 0; i < 10; i++) {
            queueWithLimit2.add(factory.generateRandomTestRecord());
        }

        queueWithLimit2.get();
        queueWithLimit2.get();
        thread2.start();

        try {
            thread2.join(10);
        } catch (InterruptedException e) {
            fail(QueueErrorMessages.INTERRUPTED_FROM_OUTSIDE.getMessage());
        }

        thread2.interrupt();
    }
}
