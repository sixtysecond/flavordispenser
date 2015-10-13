package org.sixtysecs.dispenser.turnstyle;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

import java.util.EnumSet;
import java.util.concurrent.*;

/**
 * Created by edriggs on 10/12/15.
 */
public class TurnstyleController {

    long waitTimeMillis;


    ConcurrentHashMap<TurnstyleLane, ConcurrentLinkedQueue<TurnstyleEvent>> laneEventMap =
            new ConcurrentHashMap<TurnstyleLane, ConcurrentLinkedQueue<TurnstyleEvent>>();

    public TurnstyleController( long waitTimeMillis) {

        this.waitTimeMillis = waitTimeMillis;
        initEvents();
    }

    synchronized void initEvents() {


        if (laneEventMap == null) {
            laneEventMap = new ConcurrentHashMap<TurnstyleLane, ConcurrentLinkedQueue<TurnstyleEvent>>();
        }
        for (TurnstyleLane lane : EnumSet.allOf(TurnstyleLane.class)) {
            if (laneEventMap.get(lane) == null) {
                laneEventMap.put(lane, new ConcurrentLinkedQueue<TurnstyleEvent>());
            }
            else {
                while (laneEventMap.get(lane).poll() != null);
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
                Thread.sleep(waitTimeMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            totalWaitTimeMillis += waitTimeMillis;

        }
        while (turnstyleLaneEvent == null && totalWaitTimeMillis < timeout);
        if (turnstyleLaneEvent == null) {
            throw new TimeoutException();
        }
        return turnstyleLaneEvent;
    }

    public static class WaitForCallable implements Callable<TurnstyleEvent> {

        private TurnstyleLane turnstyleLane;
        private TurnstyleController turnstyleController;
        private long timeout;
        private boolean isSuccess = false;

        public WaitForCallable(TurnstyleLane turnstyleLane, TurnstyleController turnstyleController, long timeout) {
            this.turnstyleLane = turnstyleLane;
            this.turnstyleController = turnstyleController;
            this.timeout = timeout;
        }

        public boolean isSuccess() {

            return isSuccess;
        }

        public TurnstyleEvent call() throws Exception {
            TurnstyleEvent event =  turnstyleController.waitForEvent(turnstyleLane, timeout);
            if (event != null) {
                isSuccess = true;
            }
            return event;
        }
    }

    public static class FireEventCallable implements Runnable {
        private boolean isSuccess = false;
        public boolean isSuccess() {
            return isSuccess;
        }

        private TurnstyleLane turnstyleLane;
        private TurnstyleController turnstyleController;

        public FireEventCallable(TurnstyleLane turnstyleLane, TurnstyleController turnstyleController) {
            this.turnstyleLane = turnstyleLane;
            this.turnstyleController = turnstyleController;
        }

        public void run() {
            turnstyleController.fireLaneEvent(turnstyleLane);
            isSuccess = true;
        }
    }

}
