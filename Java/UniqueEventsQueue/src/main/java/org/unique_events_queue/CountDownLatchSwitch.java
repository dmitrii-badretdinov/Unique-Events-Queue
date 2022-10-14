package org.unique_events_queue;

import java.sql.Time;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLatchSwitch {
    CountDownLatchSwitch() {
        this.positionOfAction = CountDownPosition.NONE;
        this.countDownLatch = new CountDownLatch(0);
    }

    CountDownLatchSwitch(CountDownPosition positionOfAction, int howManyTimesToCountDown) {
        this.positionOfAction = positionOfAction;
        this.countDownLatch = new CountDownLatch(howManyTimesToCountDown);
    }

    private final CountDownPosition positionOfAction;
    private final CountDownLatch countDownLatch;

    void enactStrategy(CountDownPosition currentPosition) {
        if (currentPosition.equals(positionOfAction)) {
            countDownLatch.countDown();
        }
    }

    boolean awaitCountDown(long timeout, TimeUnit timeUnit) {
        try {
            return countDownLatch.await(timeout, timeUnit);
        } catch (InterruptedException e) {
            return countDownLatch.getCount() == 0;
        }

    }
}
