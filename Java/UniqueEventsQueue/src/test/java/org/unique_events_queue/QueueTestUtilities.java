package org.unique_events_queue;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.fail;

public class QueueTestUtilities {

    /**
     * Gets an element from the queue.
     * If there is no element in the queue, throws an unchecked exception.
     * @param queue a queue to retrieve an element from.
     * @return the retrieved element
     */
    static Record getOrThrow(UniqueEventsQueue queue) {
        return queue.get(1000, true);
    }

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

    static void joinThreadAndHandleTimeout(Thread thread, long milliseconds) {
        try {
            thread.join(milliseconds);
        } catch (InterruptedException e) {
            System.out.println("The join was interrupted, which is unexpected.");
        }
    }

    // XXX temporary measure. Sleep is unappreciated. Better do countdownlatch in queue.get().
    static void sleepAndHandleInterruption(Thread thread, long milliseconds) {
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            System.out.println("Sleeping thread interrupted.");
        }
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
