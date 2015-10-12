package org.sixtysecs.dispenser;


public class DelayedWidget {

    public DelayedWidget( DelayedWidgetType delayedWidgetType) {
        this(delayedWidgetType.getDelay());
    }

    private DelayedWidget( long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
