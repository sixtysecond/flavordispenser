package org.sixtysecs;

import org.junit.Test;
import org.sixtysecs.dispenser.SelfRefillingSelectionDispenser;
import org.sixtysecs.dispenser.turnstyle.*;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.Method;
import java.util.concurrent.*;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Created by edriggs on 10/18/15.
 */
public class SelfRefillingDispenserConcurrentTest {

    ExecutorService executorService = Executors.newCachedThreadPool();

    @BeforeMethod
    public void nameBefore(Method method)
    {
        System.out.println("==== " +  getClass().getSimpleName() + "::" + method.getName() + " ====");
    }

    @Test
    public void fifoNoInventoryRequestsTest() throws InterruptedException {

        TurnstileEventFactory turnstileEventFactory = new TurnstileEventFactory(100);

        SelfRefillingSelectionDispenser<TurnstileEvent, TurnstileLane> dispenser =
                new SelfRefillingSelectionDispenser<TurnstileEvent, TurnstileLane>(turnstileEventFactory);

        DispenseCallable callable1 = new DispenseCallable(TurnstileLane.ONE, dispenser);
        DispenseCallable callable2 = new DispenseCallable(TurnstileLane.TWO, dispenser);
        DispenseCallable callable3 = new DispenseCallable(TurnstileLane.THREE, dispenser);
        executorService.submit(callable1);
        executorService.submit(callable2);
        executorService.submit(callable3);

        assertFalse(callable1.isComplete);
        assertFalse(callable2.isComplete);
        assertFalse(callable3.isComplete);

        turnstileEventFactory.fireEvent(TurnstileLane.ONE);
        Thread.sleep(40);
        assertTrue(callable1.isComplete);

        turnstileEventFactory.fireEvent(TurnstileLane.TWO);
        Thread.sleep(40);
        assertTrue(callable2.isComplete);

        turnstileEventFactory.fireEvent(TurnstileLane.THREE);
        Thread.sleep(40);
        assertTrue(callable3.isComplete);
    }

    @Test
    public void fifoNoInventoryRequestsTest2() throws InterruptedException {

        TurnstileEventFactory turnstileEventFactory = new TurnstileEventFactory(100);

        SelfRefillingSelectionDispenser<TurnstileEvent, TurnstileLane> dispenser =
                new SelfRefillingSelectionDispenser<TurnstileEvent, TurnstileLane>(turnstileEventFactory);

        DispenseCallable callable1 = new DispenseCallable(TurnstileLane.ONE, dispenser);
        DispenseCallable callable2 = new DispenseCallable(TurnstileLane.TWO, dispenser);
        DispenseCallable callable3 = new DispenseCallable(TurnstileLane.THREE, dispenser);
        executorService.submit(callable1);
        executorService.submit(callable2);
        executorService.submit(callable3);

        assertFalse(callable1.isComplete);
        assertFalse(callable2.isComplete);
        assertFalse(callable3.isComplete);

        turnstileEventFactory.fireEvent(TurnstileLane.ONE);
        Thread.sleep(40);
        assertTrue(callable1.isComplete);

        turnstileEventFactory.fireEvent(TurnstileLane.TWO);
        Thread.sleep(40);
        assertTrue(callable2.isComplete);

        turnstileEventFactory.fireEvent(TurnstileLane.THREE);
        Thread.sleep(40);
        assertTrue(callable3.isComplete);
    }

    @Test
    public void lifoNoInventoryRequestsTest() throws InterruptedException {

        TurnstileEventFactory turnstileEventFactory = new TurnstileEventFactory(100);

        SelfRefillingSelectionDispenser<TurnstileEvent, TurnstileLane> dispenser =
                new SelfRefillingSelectionDispenser<TurnstileEvent, TurnstileLane>(turnstileEventFactory);

        DispenseCallable callable1 = new DispenseCallable(TurnstileLane.ONE, dispenser);
        DispenseCallable callable2 = new DispenseCallable(TurnstileLane.TWO, dispenser);
        DispenseCallable callable3 = new DispenseCallable(TurnstileLane.THREE, dispenser);

        executorService.submit(callable3);
        executorService.submit(callable2);
        executorService.submit(callable1);

        assertFalse(callable1.isComplete);
        assertFalse(callable2.isComplete);
        assertFalse(callable3.isComplete);

        turnstileEventFactory.fireEvent(TurnstileLane.ONE);
        Thread.sleep(40);
        assertTrue(callable1.isComplete);

        turnstileEventFactory.fireEvent(TurnstileLane.TWO);
        Thread.sleep(40);
        assertTrue(callable2.isComplete);

        turnstileEventFactory.fireEvent(TurnstileLane.THREE);
        Thread.sleep(40);
        assertTrue(callable3.isComplete);

    }

    @Test
    public void dispenseNotWaitIfInventoryExistsTest() throws InterruptedException {

        TurnstileEventFactory turnstileEventFactory = new TurnstileEventFactory(100);

        SelfRefillingSelectionDispenser<TurnstileEvent, TurnstileLane> dispenser =
                new SelfRefillingSelectionDispenser<TurnstileEvent, TurnstileLane>(turnstileEventFactory);

        DispenseCallable callable1 = new DispenseCallable(TurnstileLane.ONE, dispenser);
        DispenseCallable callable2 = new DispenseCallable(TurnstileLane.TWO, dispenser);
        DispenseCallable callable3 = new DispenseCallable(TurnstileLane.THREE, dispenser);


        executorService.submit(callable3);
        executorService.submit(callable2);
        executorService.submit(callable1);

        turnstileEventFactory.fireEvent(TurnstileLane.ONE);
        turnstileEventFactory.fireEvent(TurnstileLane.TWO);
        Thread.sleep(40);

        assertTrue(callable1.isComplete);
        assertTrue(callable2.isComplete);
        assertFalse(callable3.isComplete);

        turnstileEventFactory.fireEvent(TurnstileLane.THREE);
        Thread.sleep(40);

        assertTrue(callable3.isComplete);
    }


    private class DispenseCallable implements Callable<TurnstileEvent> {

        private boolean isComplete = false;
        private TurnstileLane turnstileLane;
        private SelfRefillingSelectionDispenser<TurnstileEvent, TurnstileLane> dispenser;

        DispenseCallable(TurnstileLane turnstilLane, SelfRefillingSelectionDispenser<TurnstileEvent, TurnstileLane> dispenser) {
            this.turnstileLane = turnstilLane;
            this.dispenser = dispenser;
        }

        public TurnstileEvent call() {
            TurnstileEvent event = dispenser.dispense(turnstileLane);
            isComplete = true;
            return event;
        }

    }

}
