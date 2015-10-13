package org.sixtysecs.dispenser.turnstyle;

import java.util.Date;

/**
 * Created by edriggs on 10/11/15.
 */
public class TurnstyleEvent {


    Date firedOn = new Date();
    TurnstyleLane turnstyleLane;

    TurnstyleEvent(TurnstyleLane turnstyleLane) {
        this.turnstyleLane = turnstyleLane;
    }

    public Date getFiredOn() {
        return firedOn;
    }

    public TurnstyleLane getTurnstyleLane() {
        return turnstyleLane;
    }


}