package org.sixtysecs.dispenser.util;

import java.util.concurrent.Callable;

/**
 * Created by edriggs on 10/19/15.
 */

public abstract class BlockedCallable<T> implements Callable {

    long timeout;
    long pollInterval;
    boolean isBlocked = true;

    public BlockedCallable(long timeout, long pollInterval) {
        this.timeout = timeout;
        this.pollInterval = pollInterval;
    }

    public final T call() {
        for (long elapsed = 0; isBlocked && elapsed < timeout; elapsed += pollInterval) {
            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return doCall();
    }

    public abstract T doCall();
}
