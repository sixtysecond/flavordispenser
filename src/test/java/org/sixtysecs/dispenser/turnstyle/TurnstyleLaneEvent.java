package org.sixtysecs.dispenser.turnstyle;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by edriggs on 10/11/15.
 */
public class TurnstyleLaneEvent {

    static long waitTimeMillis = 10;
    static ConcurrentHashMap<TurnstyleLane, ConcurrentLinkedQueue<TurnstyleLaneEvent>> laneEventMap =
            new ConcurrentHashMap<TurnstyleLane, ConcurrentLinkedQueue<TurnstyleLaneEvent>>();

    static {
        initEvents();
    }

    Date firedOn = new Date();
    TurnstyleLane turnstyleLane;

    private TurnstyleLaneEvent(TurnstyleLane turnstyleLane) {
        this.turnstyleLane = turnstyleLane;
    }



    static synchronized void initEvents() {


        if (laneEventMap == null) {
            laneEventMap = new ConcurrentHashMap<TurnstyleLane, ConcurrentLinkedQueue<TurnstyleLaneEvent>>();
        }
        for (TurnstyleLane lane : EnumSet.allOf(TurnstyleLane.class)) {
            if (laneEventMap.get(lane) == null) {
                laneEventMap.put(lane, new ConcurrentLinkedQueue<TurnstyleLaneEvent>());
            }
            else {
                while (laneEventMap.get(lane).poll() != null);
            }
        }
    }

    static synchronized void fireLaneEvent(TurnstyleLane lane) {
        TurnstyleLaneEvent event = new TurnstyleLaneEvent(lane);
        laneEventMap.get(lane)
                .add(event);
    }

    public static TurnstyleLaneEvent waitForEvent(TurnstyleLane lane, long timeout) throws TimeoutException {
        TurnstyleLaneEvent turnstyleLaneEvent = null;
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

    public Date getFiredOn() {
        return firedOn;
    }

    public TurnstyleLane getTurnstyleLane() {
        return turnstyleLane;
    }

    public static class WaitForCallable implements Callable<TurnstyleLaneEvent> {

        private TurnstyleLane turnstyleLane;
        private long timeout;
        private boolean isSuccess = false;

        public WaitForCallable(TurnstyleLane turnstyleLane, long timeout) {
            this.turnstyleLane = turnstyleLane;
            this.timeout = timeout;
        }

        public boolean isSuccess() {

            return isSuccess;
        }

        public TurnstyleLaneEvent call() throws Exception {
            TurnstyleLaneEvent event =  TurnstyleLaneEvent.waitForEvent(turnstyleLane, timeout);
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

        public FireEventCallable(TurnstyleLane turnstyleLane) {
            this.turnstyleLane = turnstyleLane;
        }

        public void run() {
            TurnstyleLaneEvent.fireLaneEvent(turnstyleLane);
            isSuccess = true;
        }
    }
}