package org.unique_events_queue.tests;

import org.junit.jupiter.api.Test;
import org.unique_events_queue.Record;
import org.unique_events_queue.ThreadInfoProvider;
import org.unique_events_queue.UniqueEventsQueue;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class UniqueEventsQueueConcurrencyTest {
    static final RecordFactory factory = new RecordFactory(new RecordFactorySettings());

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
    void testThatQueueTrimsIfTrimIntervalIs1() {
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

    @Test
    void testThatQueueTrimsForAdd() {
        long numberOfRecords = 50;
        UniqueEventsQueue queue = new UniqueEventsQueue(1, numberOfRecords,
            new ThreadInfoProvider(1));

        for(int i = 0; i < numberOfRecords; i++) {
            queue.add(factory.generateRandomTestRecord());
        }

        queue.get();
        assertThat(checkThatQueueIsEmpty(queue)).isEqualTo(true);
    }

    @Test
    void testThatQueueTrimsForAddAll() {
        long numberOfRecords = 50;
        UniqueEventsQueue queue = new UniqueEventsQueue(1, numberOfRecords, new ThreadInfoProvider(1));
        List<Record> firstRecordList = new LinkedList<>();
        List<Record> secondRecordList = new LinkedList<>();

        for(int i = 0; i < numberOfRecords; i++) {
            firstRecordList.add(factory.generateRandomTestRecord());
            secondRecordList.add(factory.generateRandomTestRecord());
        }

        queue.addAll(firstRecordList);
        queue.addAll(secondRecordList);
        Iterator<Record> iterator = secondRecordList.iterator();
        for(int i = 0; i < secondRecordList.size(); i++) {
            assertThat(queue.get()).isEqualTo(iterator.next());
        }
    }

    @Test
    void testThatQueueDoesNotTrimWhenTrimIntervalIsNotReachedForAdd() {
        long numberOfRecords = 50;
        UniqueEventsQueue queue = new UniqueEventsQueue(1,
            numberOfRecords + 1, new ThreadInfoProvider(1));

        for(int i = 0; i < numberOfRecords; i++) {
            queue.add(factory.generateRandomTestRecord());
        }

        drainRecords(queue, numberOfRecords);
    }

    @Test
    void testThatQueueDoesNotTrimWhenTrimIntervalIsNotReachedForAddAll() {
        long numberOfRecords = 50;
        UniqueEventsQueue queue = new UniqueEventsQueue(1,
            numberOfRecords + 1, new ThreadInfoProvider(1));
        List<Record> recordList = new LinkedList<>();

        for(int i = 0; i < numberOfRecords; i++) {
            recordList.add(factory.generateRandomTestRecord());
        }

        queue.addAll(recordList);
        drainRecords(queue, numberOfRecords);
        assertThat(checkThatQueueIsEmpty(queue)).isEqualTo(true);
    }

    private static void drainRecords(UniqueEventsQueue queue, long numberOfRecords) {
        Runnable runnable = queue::get;
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<?>> futureList = new LinkedList<>();

        for(int i = 0; i < numberOfRecords; i++) {
            futureList.add(executor.submit(runnable));
        }
        for(Future<?> future : futureList) {
            try {
                future.get(1, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ExecutionException e) {
                fail(getMessageAboutInterruptionFromTheOutside());
            } catch (TimeoutException e) {
                fail(getMessageAboutTimeout());
            }
        }
    }

    private static boolean checkThatQueueIsEmpty(UniqueEventsQueue queue) {
        Callable<Record> callable = queue::get;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Record> future = executor.submit(callable);
        boolean queueIsEmpty = false;

        try {
            future.get(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            fail(getMessageAboutInterruptionFromTheOutside());
        } catch (TimeoutException e) {
            queueIsEmpty = true;
            executor.shutdownNow();
        }

        return queueIsEmpty;
    }
    private static String getMessageAboutInterruptionFromTheOutside() {
        return "Test thread was interrupted from the outside.";
    }

    private static String getMessageAboutTimeout() {
        return "Thread took too long to return a value.";
    }
}
