package org.sixtysecs.dispenser.turnstyle;

/**
 * Created by edriggs on 10/12/15.
 */
public class FireTurnstyleEventRunnable implements Runnable{
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
