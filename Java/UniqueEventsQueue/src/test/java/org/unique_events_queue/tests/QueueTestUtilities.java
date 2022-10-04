package org.unique_events_queue.tests;

import org.unique_events_queue.Record;
import org.unique_events_queue.UniqueEventsQueue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.fail;

public class QueueTestUtilities {
    static <T> T getFuture(Future<T> future) {
        T result = null;
        try {
            result = future.get(50, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            fail(QueueErrorMessages.INTERRUPTED_FROM_OUTSIDE.getMessage());
        } catch (TimeoutException e) {
            fail(QueueErrorMessages.THREAD_TIMEOUT.getMessage());
        }

        return result;
    }

    static void drainRecords(UniqueEventsQueue queue, long numberOfRecords) {
        Runnable runnable = queue::get;
        ExecutorService executor = Executors.newCachedThreadPool();
        List<Future<?>> futureList = new LinkedList<>();

        for(int i = 0; i < numberOfRecords; i++) {
            futureList.add(executor.submit(runnable));
        }
        for(Future<?> future : futureList) {
            getFuture(future);
        }
    }

    static boolean queueIsEmpty(UniqueEventsQueue queue) {
        Callable<Record> callable = queue::get;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Record> future = executor.submit(callable);
        boolean queueIsEmpty = false;

        try {
            future.get(1, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            fail(QueueErrorMessages.INTERRUPTED_FROM_OUTSIDE.getMessage());
        } catch (TimeoutException e) {
            queueIsEmpty = true;
            executor.shutdownNow();
        }

        return queueIsEmpty;
    }
}
