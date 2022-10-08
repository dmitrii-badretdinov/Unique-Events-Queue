package org.unique_events_queue;

import java.sql.Time;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

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
    private long elementsInsertedAfterLastTrim;
    private final long trimAfterThatManyInsertedElements;
    private final ConcurrentHashMap<Object, Boolean> waitingThreadsMap = new ConcurrentHashMap<>();

    /**
     * Creates an instance with a default parameters.
     */
    public UniqueEventsQueue(){
        this((long) Math.pow(10, 9), 1000);
    }

    /**
     * Creates an instance with a specified queue limit.
     *
     * @param queueLimitParameter a queue limit.
     * @param trimAfterThatManyInsertedElements every that many items, it is checked if the queue size exceeds
     * queueLimitParameter. In other words, the queue size may exceed the limit by that many elements at most.
     * @throws IllegalArgumentException if queue limit is less than 1
     */
    public UniqueEventsQueue(long queueLimitParameter, long trimAfterThatManyInsertedElements)
        throws IllegalArgumentException {
        if(queueLimitParameter < 1)
        {
            throw new IllegalArgumentException("Queue size cannot be 0 or negative.");
        }
        queueLimit = queueLimitParameter;
        this.trimAfterThatManyInsertedElements = trimAfterThatManyInsertedElements;
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
                elementsInsertedAfterLastTrim++;
                if(elementsInsertedAfterLastTrim >= trimAfterThatManyInsertedElements) {
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
        /*
         * addAll() assumes that it receives an immutable list.
         * It is advised to send an immutable list to addAll to prevent an attack on its contents during the transfer.
         */
        synchronized (lockForAddGet) {
            if (recordList != null) {
                /*
                 * The current trimming strategy is to preemptively trim the queue
                 * as if all elements will be inserted into it.
                 */
                if(elementsInsertedAfterLastTrim + recordList.size() >= trimAfterThatManyInsertedElements) {
                    trimQueueToGivenLimit(recordList.size());
                }

                long numberOfItemsInserted = 0;
                for (Record record : recordList) {
                    if(record != null && queue.add(record)) {
                        numberOfItemsInserted++;
                    }
                }
                elementsInsertedAfterLastTrim += numberOfItemsInserted;

                long howManyTreadsToNotify =
                    numberOfItemsInserted > waitingThreadsMap.size() ? waitingThreadsMap.size() : numberOfItemsInserted;
                for(int i = 0; i < howManyTreadsToNotify; i++) {
                    lockForAddGet.notify();
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
        return get(Long.MAX_VALUE, false);
    }

    /**
     * Retrieves a record from the queue.
     *
     * @param milliseconds wait for how many milliseconds before waking up and checking the queue state again.
     *                     Zero equals endless wait.
     * @param shouldItThrow a flag to allow throwing a TimeoutException if the waiting time ran out.
     * @return a Record from the queue on the FIFO principle.
     */
    Record get(long milliseconds, boolean shouldItThrow) {
        /*
         * This function throws an unchecked RuntimeException instead of a checked TimeoutException because
         * the said exception is expected to be used only in tests.
         * Any public function should call this function with the shouldItThrow set to false,
         * so it does not throw the unchecked exception.
         */
        synchronized (lockForAddGet) {
            Record recordToReturn;
            long timeElapsed = 0;

            try {
                timeElapsed = System.nanoTime();
                while (!queue.iterator().hasNext()) {
                    waitingThreadsMap.putIfAbsent(this, true);
                    lockForAddGet.wait(milliseconds);

                    if (shouldItThrow && System.nanoTime() - timeElapsed >= milliseconds * Math.pow(10, 6)) {
                        throw new RuntimeException("Timed out. There were no elements in the queue.");
                    }
                }

                recordToReturn = queue.iterator().next();
                queue.remove(recordToReturn);
                waitingThreadsMap.remove(this);
            } catch (InterruptedException e) {
                waitingThreadsMap.remove(this);
                return null;
            }

            return recordToReturn;
        }
    }

    /**
     * Indicates if queue contains anything.
     *
     * @return true if the queue is empty. False otherwise.
     */
    boolean isEmpty() {
        synchronized (lockForAddGet) {
            return queue.isEmpty();
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

            if (queueSizeAfterAddition > queueLimit) {

                long numberOfItemsToRemove = queueSizeAfterAddition - queueLimit;
                Iterator<Record> iterator = queue.iterator();

                for (int i = 0; iterator.hasNext() && i < numberOfItemsToRemove; i++) {
                    iterator.next();
                    iterator.remove();
                }
            }
            elementsInsertedAfterLastTrim = 0;
        }
    }
}
