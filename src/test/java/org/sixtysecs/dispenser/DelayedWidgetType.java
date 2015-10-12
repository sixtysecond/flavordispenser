package org.sixtysecs.dispenser;

/**
 * Created by edriggs on 10/11/15.
 */
public enum DelayedWidgetType {
    DELAY_1ms(1),//
    DELAY_10ms(10),//
    DELAY_100ms(100),//
    DELAY_1000ms(1000),//
    ;

    private int delay;

    private DelayedWidgetType(int delay) {
        this.delay = delay;
    }

    public int getDelay() {
        return delay;
    }
}
