package org.example;

import java.util.*;
import java.util.concurrent.CountDownLatch;

/*
 * The class assumes that the entries are immutable.
 * Because of that, safe copying of separate entries is not done when accepting the parameters.
 */
public final class UniqueEventsQueue {
    private final LinkedHashSet<Record> queue = new LinkedHashSet<>();
    private final Object lockForAddGet = new Object();
    private final long queueLimit;

    public UniqueEventsQueue(){
        this(UniqueEventsQueue.calculateQueueLimit());
    }

    public UniqueEventsQueue(long queueLimitParameter) throws IllegalArgumentException {
        if(queueLimitParameter < 1)
        {
            throw new IllegalArgumentException("Queue size cannot be 0 or negative.");
        }
        queueLimit = queueLimitParameter;
    }

    public void add(Record record) {
        synchronized (lockForAddGet) {
            if (record != null && queue.add(record)) {
                trimQueueToGivenLimit();
                lockForAddGet.notify();
            }
        }
    }

    /*
     * addAll assumes that it receives an immutable list.
     */
    public void addAll(List<Record> recordList) {
        synchronized (lockForAddGet) {
            if (recordList != null) {
                for (Record record : recordList) {
                    if(record != null && queue.add(record)) {
                        trimQueueToGivenLimit();
                        lockForAddGet.notify();
                    }
                }
            }
        }
    }

    public Record get() {
        synchronized (lockForAddGet) {
            Record recordToReturn = null;

            try {
                while (!queue.iterator().hasNext()) {
                    lockForAddGet.wait();
                }

                recordToReturn = queue.iterator().next();
                queue.remove(recordToReturn);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return recordToReturn;
        }
    }

    private void trimQueueToGivenLimit() {
        synchronized (lockForAddGet) {
            if (!queue.isEmpty() && queue.size() > queueLimit) {
                if (queue.size() - queueLimit == 1) {
                    queue.remove(queue.iterator().next());
                } else {
                    long numberOfItemsToRemove = queue.size() - queueLimit;
                    Iterator<Record> iterator = queue.iterator();
                    for (int i = 0; i < numberOfItemsToRemove; i++) {
                        if(iterator.hasNext()) {
                            iterator.remove();
                        }
                    }
                }
            }
        }
    }

    private static long calculateQueueLimit() {
        // totalMemory is the memory currently given to the program.
        // freeMemory is currently free memory in totalMemory.
        // maxMemory is the maximum memory that can be given to the program.
        long allocatedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long actualFreeMemory = Runtime.getRuntime().maxMemory() - allocatedMemory;
        //This step is likely done through Instrumentation, but I'm not sure how to do it properly.
        // So the next line is a plug.
        long objectSize = 30;
        return actualFreeMemory / objectSize;
    }
}
