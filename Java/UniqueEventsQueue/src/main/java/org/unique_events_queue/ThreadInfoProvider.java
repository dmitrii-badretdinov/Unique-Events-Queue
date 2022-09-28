package org.unique_events_queue;

public class ThreadInfoProvider implements IThreadInfoProvider {
    public ThreadInfoProvider(int maxNumberOfGetterThreads) {
        numberOfThreads = maxNumberOfGetterThreads;
    }

    private int numberOfThreads;

    public int retrieveTheNumberOfGetThreads() {
        // In the actual system, the provider would pool the data or would make a request to somewhere else.
        return numberOfThreads;
    }
}
