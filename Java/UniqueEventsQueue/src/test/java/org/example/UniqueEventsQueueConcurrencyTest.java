package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class UniqueEventsQueueConcurrencyTest {
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
    void testThatAddAllNotifiesAllWaitingThreads() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        Runnable runnableTask = queue::get;
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        threadPoolExecutor.setCorePoolSize(0);
        threadPoolExecutor.setMaximumPoolSize(50);
        List<Record> records = new LinkedList<>();
        for(int i = 0; i < 50; i++) {
            threadPoolExecutor.submit(runnableTask);
            records.add(factory.generateRandomTestRecord());
        }
        queue.addAll(records);
        threadPoolExecutor.shutdown();
        try {
            if(!threadPoolExecutor.awaitTermination(50, TimeUnit.MILLISECONDS)) {
                fail("Threads failed to get the Records in specified timeframe.");
            }
        } catch (InterruptedException e) {
            System.out.println("The wait for thread pool to shutdown was interrupted.");
        }
    }

    @Test
    void testThatGetThrowsNoExceptionIfQueueBecomesEmpty() {
        UniqueEventsQueue uniqueEventsQueue = new UniqueEventsQueue();
        Callable<Record> callable = uniqueEventsQueue::get;
        Runnable runnable = uniqueEventsQueue::get;
        ExecutorService executorThread1 = Executors.newSingleThreadExecutor();
        Future<Record> future = executorThread1.submit(callable);
        Thread thread2 = new Thread(runnable);
        Record result = null;
        Record record = factory.generateRandomTestRecord();

        uniqueEventsQueue.add(record);

        try {
            result = future.get(5, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            fail("Test thread was interrupted.");
        } catch (TimeoutException e) {
            fail("Thread took too long to return a value.");
        }

        thread2.start();

        try {
            thread2.join(10);
        } catch (InterruptedException e) {
            fail("Test thread was interrupted.");
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
            fail("Test thread was interrupted.");
        }
        thread.interrupt();
    }

    @Test
    void testThatQueueTrimsItselfToSpecifiedSize() {
        UniqueEventsQueue queueWithLimit1 = new UniqueEventsQueue(1);
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
            fail("Test thread was interrupted.");
        }
        thread1.interrupt();

        UniqueEventsQueue queueWithLimit2 = new UniqueEventsQueue(2);
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
            fail("Test thread was interrupted.");
        }

        thread2.interrupt();
    }
}
