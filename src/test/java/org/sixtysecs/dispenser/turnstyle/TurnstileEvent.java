package org.sixtysecs.dispenser.turnstyle;

import java.util.Date;

/**
 * Created by edriggs on 10/11/15.
 */
public class TurnstileEvent {

    private Date firedOn = new Date();
    private TurnstileLane lane;

    TurnstileEvent(TurnstileLane lane) {
        this.lane = lane;
    }

    public Date getFiredOn() {
        return firedOn;
    }

    public TurnstileLane getLane() {
        return lane;
    }
}