package org.sixtysecs.dispenser.turnstyle;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

import java.util.EnumSet;
import java.util.concurrent.*;

/**
 * Used for concurrency testing. Allows
 */
public class TurnstileController {

    private long pollWaitMillis = 10;

    private ConcurrentHashMap<TurnstileLane, ConcurrentLinkedQueue<TurnstileEvent>> laneEvents;

    public TurnstileController() {
        laneEvents = new ConcurrentHashMap<TurnstileLane, ConcurrentLinkedQueue<TurnstileEvent>>();
        for (TurnstileLane lane : EnumSet.allOf(TurnstileLane.class)) {
            laneEvents.put(lane, new ConcurrentLinkedQueue<TurnstileEvent>());
        }
    }

    public long getPollWaitMillis() {
        return pollWaitMillis;
    }

    public TurnstileController setPollWaitMillis(long pollWaitMillis) {
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
}
