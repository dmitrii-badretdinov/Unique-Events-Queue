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
    static final ThreadInfoProvider oneThreadStub = new ThreadInfoProvider(1);

    @Test
    void testThatAddAllNotifiesAllWaitingThreads() {
        UniqueEventsQueue queue = new UniqueEventsQueue(new ThreadInfoProvider(50));
        Runnable runnableTask = queue::get;
        ThreadPoolExecutor mockThreadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
        mockThreadPoolExecutor.setCorePoolSize(0);
        mockThreadPoolExecutor.setMaximumPoolSize(50);
        List<Record> recordList = new LinkedList<>();

        for(int i = 0; i < 50; i++) {
            mockThreadPoolExecutor.submit(runnableTask);
            recordList.add(factory.generateRandomFakeRecord());
        }

        queue.addAll(recordList);
        mockThreadPoolExecutor.shutdown();

        try {
            if(!mockThreadPoolExecutor.awaitTermination(50, TimeUnit.MILLISECONDS)) {
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
        Record mockResult = null;
        Record mockRecord = factory.generateRandomFakeRecord();

        uniqueEventsQueue.add(mockRecord);
        
        Future<Record> future = executorThread1.submit(callable);

        try {
            mockResult = future.get(5, TimeUnit.SECONDS);
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

        assertThat(mockResult).isEqualTo(mockRecord);
        thread2.interrupt();
    }

    @Test
    void testThatGetThrowsNoExceptionIfQueueIsAlwaysEmpty() {
        UniqueEventsQueue queue = new UniqueEventsQueue();
        Runnable runnable = queue::get;
        Thread mockThread = new Thread(runnable);

        mockThread.start();
        try {
            mockThread.join(10);
        } catch (InterruptedException e) {
            fail(QueueErrorMessages.INTERRUPTED_FROM_OUTSIDE.getMessage());
        }
        mockThread.interrupt();
    }

    @Test
    void testThatQueueTrimsIfTrimIntervalIs1() {
        UniqueEventsQueue queueWithLimit1 = new UniqueEventsQueue(1, 1,
            oneThreadStub);

        Runnable runnable = queueWithLimit1::get;
        Thread thread1 = new Thread(runnable);

        for(int i = 0; i < 10; i++) {
            queueWithLimit1.add(factory.generateRandomFakeRecord());
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
            oneThreadStub);
        Thread thread2 = new Thread(runnable);

        for(int i = 0; i < 10; i++) {
            queueWithLimit2.add(factory.generateRandomFakeRecord());
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
        UniqueEventsQueue mockQueue = new UniqueEventsQueue(1, numberOfRecords,
                oneThreadStub);

        for(int i = 0; i < numberOfRecords; i++) {
            mockQueue.add(factory.generateRandomFakeRecord());
        }

        mockQueue.get();
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
