package org.example;

enum QueueErrorMessages {
    TIMEOUT("The thread took too long to return a value."),
    INTERRUPTED_FROM_OUTSIDE("The test thread was interrupted from the outside.");

    private String message;

    private QueueErrorMessages(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
