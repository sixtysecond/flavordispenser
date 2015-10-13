package org.sixtysecs.dispenser.turnstyle;

import java.util.Date;

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