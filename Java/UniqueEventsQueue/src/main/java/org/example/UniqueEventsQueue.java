package org.example;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A thread-safe queue of unique elements.
 * It works on the First-In-First-Out (FIFO) principle.
 */
public final class UniqueEventsQueue {
    /*
     * The class assumes that the entries are immutable.
     * Because of that, safe copying of separate entries is not done when accepting the parameters.
     */
    private final LinkedHashSet<Record> queue = new LinkedHashSet<>();
    private final Object lockForAddGet = new Object();
    private final long queueLimit;
    private long elementsIntertedAfterLastTrim;
    private final long trimAfterHowManyInsertedElements;

    /**
     * Creates an instance with a default queue limit.
     */
    public UniqueEventsQueue(){
        this((long) Math.pow(10, 9), 1000);
    }

    /**
     * Creates an instance with a specified queue limit.
     *
     * @param queueLimitParameter a queue limit.
     * @param trimAfterHowManyInsertedElements every that many items, it is checked if the queue size exceeds
     * queueLimitParameter. In other words, the queue size may exceed the limit by that many elements at most.
     * @throws IllegalArgumentException if queue limit is less than 1
     */
    public UniqueEventsQueue(long queueLimitParameter, long trimAfterHowManyInsertedElements) throws IllegalArgumentException {
        if(queueLimitParameter < 1)
        {
            throw new IllegalArgumentException("Queue size cannot be 0 or negative.");
        }
        queueLimit = queueLimitParameter;
        this.trimAfterHowManyInsertedElements = trimAfterHowManyInsertedElements;
    }

    /**
     * Puts a record into the queue.
     *
     * @param record the record to put into the queue
     */
    public void add(Record record) {
        synchronized (lockForAddGet) {
            if (record != null && queue.add(record)) {
                lockForAddGet.notify();
                if(elementsIntertedAfterLastTrim >= trimAfterHowManyInsertedElements) {
                    trimQueueToGivenLimit(0);
                }
            }
        }
    }

    /**
     * Adds a list of records into the queue.
     *
     * @param recordList a list of records to put into the queue.
     */
    public void addAll(List<Record> recordList) {
        // addAll assumes that it receives an immutable list.
        // It is advised to send an immutable list to addAll to prevent an attack on its contents during the transfer.
        synchronized (lockForAddGet) {
            if (recordList != null) {
                if(elementsIntertedAfterLastTrim + recordList.size() >= trimAfterHowManyInsertedElements) {
                    trimQueueToGivenLimit(recordList.size());
                }
                for (Record record : recordList) {
                    if(record != null && queue.add(record)) {
                        lockForAddGet.notify();
                    }
                }
            }
        }
    }

    /**
     * Retrieves the oldest record from the queue. FIFO principle.
     *
     * @return the oldest record from the queue.
     */
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

    /**
     * Trims the queue to the specified limit.
     *
     * @param howManyTheoreticallyAdded adds that many items to the current queue size. Works as a proactive trim before
     * the queue exceeds the limit.
     */
    private void trimQueueToGivenLimit(long howManyTheoreticallyAdded) {
        synchronized (lockForAddGet) {
            long queueSizeAfterAddition = queue.size() + howManyTheoreticallyAdded;
            if (!queue.isEmpty() && queueSizeAfterAddition > queueLimit) {
                long numberOfItemsToRemove = queueSizeAfterAddition - queueLimit;
                Iterator<Record> iterator = queue.iterator();
                for (int i = 0; i < numberOfItemsToRemove; i++) {
                    if(iterator.hasNext()) {
                        iterator.remove();
                    }
                }
            }
            elementsIntertedAfterLastTrim = 0;
        }
    }
}
