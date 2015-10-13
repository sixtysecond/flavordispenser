package org.sixtysecs.dispenser.turnstyle;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

import java.util.EnumSet;
import java.util.concurrent.*;

/**
 * Created by edriggs on 10/12/15.
 */
public class TurnstyleController {

    private long pollWaitMillis = 10;

    private ConcurrentHashMap<TurnstyleLane, ConcurrentLinkedQueue<TurnstyleEvent>> laneEventMap =
            new ConcurrentHashMap<TurnstyleLane, ConcurrentLinkedQueue<TurnstyleEvent>>();

    public TurnstyleController() {
        initEvents();
    }

    public long getPollWaitMillis() {
        return pollWaitMillis;
    }

    public TurnstyleController setPollWaitMillis(long pollWaitMillis) {
        if (pollWaitMillis < 0) {
            pollWaitMillis = 0;
        }
        this.pollWaitMillis = pollWaitMillis;
        return this;
    }

    private synchronized void initEvents() {


        if (laneEventMap == null) {
            laneEventMap = new ConcurrentHashMap<TurnstyleLane, ConcurrentLinkedQueue<TurnstyleEvent>>();
        }
        for (TurnstyleLane lane : EnumSet.allOf(TurnstyleLane.class)) {
            if (laneEventMap.get(lane) == null) {
                laneEventMap.put(lane, new ConcurrentLinkedQueue<TurnstyleEvent>());
            } else {
                while (laneEventMap.get(lane)
                        .poll() != null) ;
            }
        }
    }

    synchronized void fireLaneEvent(TurnstyleLane lane) {
        TurnstyleEvent event = new TurnstyleEvent(lane);
        laneEventMap.get(lane)
                .add(event);
    }


    public TurnstyleEvent waitForEvent(TurnstyleLane lane, long timeout) throws TimeoutException {
        TurnstyleEvent turnstyleLaneEvent = null;
        int totalWaitTimeMillis = 0;
        do {
            turnstyleLaneEvent = laneEventMap.get(lane)
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

    public static class WaitForTurnstyleEventCallable implements Callable<TurnstyleEvent> {

        private TurnstyleLane turnstyleLane;
        private TurnstyleController turnstyleController;
        private long timeout;
        private boolean isSuccess = false;

        public WaitForTurnstyleEventCallable(TurnstyleLane turnstyleLane, TurnstyleController turnstyleController, long timeout) {
            this.turnstyleLane = turnstyleLane;
            this.turnstyleController = turnstyleController;
            this.timeout = timeout;
        }

        public boolean isSuccess() {

            return isSuccess;
        }

        public TurnstyleEvent call() throws Exception {
            TurnstyleEvent event = turnstyleController.waitForEvent(turnstyleLane, timeout);
            if (event != null) {
                isSuccess = true;
            }
            return event;
        }
    }

    public static class FireTurnstyleEventRunnable implements Runnable {
        private boolean isSuccess = false;
        private TurnstyleLane turnstyleLane;
        private TurnstyleController turnstyleController;
        public FireTurnstyleEventRunnable(TurnstyleLane turnstyleLane, TurnstyleController turnstyleController) {
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
