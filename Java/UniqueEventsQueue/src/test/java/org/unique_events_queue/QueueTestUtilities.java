package org.unique_events_queue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.fail;

public class QueueTestUtilities {
    static <T> T getFutureAndHandleExceptions(Future<T> future) {
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
            getFutureAndHandleExceptions(future);
        }
    }
}
