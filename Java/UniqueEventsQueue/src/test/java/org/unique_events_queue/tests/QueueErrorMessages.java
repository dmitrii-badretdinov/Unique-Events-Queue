package org.unique_events_queue.tests;

enum QueueErrorMessages {
    INTERRUPTED_FROM_OUTSIDE("Interrupted from the outside."),
    EXECUTOR_TIMEOUT("Executor failed to shutdown within specified timeframe."),
    THREAD_TIMEOUT("Thread took too long to return a value.");

    private String message;

    QueueErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
