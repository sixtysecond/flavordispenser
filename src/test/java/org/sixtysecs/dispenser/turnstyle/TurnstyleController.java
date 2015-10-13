package org.sixtysecs.dispenser.turnstyle;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

import java.util.EnumSet;
import java.util.concurrent.*;

/**
 * Created by edriggs on 10/12/15.
 */
public class TurnstyleController {

    private long pollWaitMillis = 10;

    private ConcurrentHashMap<TurnstileLane, ConcurrentLinkedQueue<TurnstileEvent>> laneEvents;

    public TurnstyleController() {
        laneEvents = new ConcurrentHashMap<TurnstileLane, ConcurrentLinkedQueue<TurnstileEvent>>();
        for (TurnstileLane lane : EnumSet.allOf(TurnstileLane.class)) {
            laneEvents.put(lane, new ConcurrentLinkedQueue<TurnstileEvent>());
        }
    }

    public long getPollWaitMillis() {
        return pollWaitMillis;
    }

    public TurnstyleController setPollWaitMillis(long pollWaitMillis) {
        if (pollWaitMillis < 0) {
            pollWaitMillis = 1;
        }
        this.pollWaitMillis = pollWaitMillis;
        return this;
    }

    synchronized void fireLaneEvent(TurnstileLane lane) {
        TurnstileEvent event = new TurnstileEvent(lane);
        laneEvents.get(lane)
                .add(event);
    }


    public TurnstileEvent waitForEvent(TurnstileLane lane, long timeout) throws TimeoutException {
        TurnstileEvent turnstyleLaneEvent = null;
        int totalWaitTimeMillis = 0;
        do {
            turnstyleLaneEvent = laneEvents.get(lane)
                    .poll();
            if (turnstyleLaneEvent != null) {
                break;
            }
            try {
                Thread.sleep(pollWaitMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            totalWaitTimeMillis += pollWaitMillis;

        }
        while (turnstyleLaneEvent == null && totalWaitTimeMillis < timeout);
        if (turnstyleLaneEvent == null) {
            throw new TimeoutException();
        }
        return turnstyleLaneEvent;
    }

    public static class WaitForTurnstyleEventCallable implements Callable<TurnstileEvent> {

        private TurnstileLane turnstyleLane;
        private TurnstyleController turnstyleController;
        private long timeout;
        private boolean isSuccess = false;

        public WaitForTurnstyleEventCallable(TurnstileLane turnstyleLane, TurnstyleController turnstyleController, long timeout) {
            this.turnstyleLane = turnstyleLane;
            this.turnstyleController = turnstyleController;
            this.timeout = timeout;
        }

        public boolean isSuccess() {

            return isSuccess;
        }

        public TurnstileEvent call() throws Exception {
            TurnstileEvent event = turnstyleController.waitForEvent(turnstyleLane, timeout);
            if (event != null) {
                isSuccess = true;
            }
            return event;
        }
    }

    public static class FireTurnstyleEventRunnable implements Runnable {
        private boolean isSuccess = false;
        private TurnstileLane turnstyleLane;
        private TurnstyleController turnstyleController;

        public FireTurnstyleEventRunnable(TurnstileLane turnstyleLane, TurnstyleController turnstyleController) {
            this.turnstyleLane = turnstyleLane;
            this.turnstyleController = turnstyleController;
        }

        public boolean isSuccess() {
            return isSuccess;
        }

        public void run() {
            turnstyleController.fireLaneEvent(turnstyleLane);
            isSuccess = true;
        }
    }

}
