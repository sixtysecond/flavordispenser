package org.sixtysecs.dispenser.turnstyle;

import java.util.concurrent.Callable;

/**
 * Created by edriggs on 10/12/15.
 */
public class WaitForTurnstyleEventCallable implements Callable {
    private TurnstileLane turnstyleLane;
    private TurnstileController turnstyleController;
    private long timeout;
    private boolean isSuccess = false;

    public WaitForTurnstyleEventCallable(TurnstileLane turnstyleLane, TurnstileController turnstyleController, long timeout) {
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
