package org.sixtysecs.dispenser.util;

/**
 * Created by edriggs on 10/19/15.
 */

public abstract class BlockedRunnable implements Runnable {

    long timeout;
    long pollInterval;
    boolean isBlocked = true;

    public BlockedRunnable(long timeout, long pollInterval) {
        this.timeout = timeout;
        this.pollInterval = pollInterval;
    }

    public final void run() {
        for (long elapsed = 0; isBlocked && elapsed < timeout; elapsed += pollInterval) {
            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        doRun();
    }

    public abstract void doRun();
}
