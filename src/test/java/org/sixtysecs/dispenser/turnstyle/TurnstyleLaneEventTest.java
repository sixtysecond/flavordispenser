package org.sixtysecs.dispenser.turnstyle;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.*;

/**
 * Created by edriggs on 10/11/15.
 */
@Test(singleThreaded=true, threadPoolSize=1)
public class TurnstyleLaneEventTest {


    @Test(expectedExceptions = {TimeoutException.class})
    public void waitTimeouExceededTest() throws TimeoutException {
        TurnstyleController turnstyleController = new TurnstyleController();
        TurnstileEvent turnstyleLaneEvent = turnstyleController.waitForEvent(TurnstileLane.ONE, 0l);
    }

    @Test
    public void fireEventBeforeWaitTest() throws TimeoutException {
        TurnstyleController turnstyleController = new TurnstyleController();
        final TurnstileLane lane = TurnstileLane.ONE;
        turnstyleController.fireLaneEvent(lane);
        TurnstileEvent event = turnstyleController.waitForEvent(lane, 0l);
        Assert.assertNotNull(event);
        Assert.assertEquals(event.getLane(), lane);
    }

    @Test
    public void fireEventBeforeTimeoutTest() throws TimeoutException, InterruptedException {
        TurnstyleController turnstyleController = new TurnstyleController();
        final TurnstileLane lane = TurnstileLane.ONE;

        TurnstyleController.WaitForTurnstyleEventCallable waitForCallable =
                new TurnstyleController.WaitForTurnstyleEventCallable(lane, turnstyleController, 100);
        TurnstyleController.FireTurnstyleEventRunnable fireEventCallable = new TurnstyleController.FireTurnstyleEventRunnable(lane, turnstyleController);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(waitForCallable);

        //sleep for less than timeout
        Thread.sleep(20);

        executor.submit(fireEventCallable);
        executor.shutdown();
        executor.awaitTermination(2000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(waitForCallable.isSuccess());
        Assert.assertTrue(fireEventCallable.isSuccess());
    }

    @Test
    public void fireEventAfterTimeoutTest() throws TimeoutException, InterruptedException {
        TurnstyleController turnstyleController = new TurnstyleController();
        final TurnstileLane lane = TurnstileLane.ONE;

        TurnstyleController.WaitForTurnstyleEventCallable waitForCallable = new TurnstyleController.WaitForTurnstyleEventCallable(lane, turnstyleController, 100);
        TurnstyleController.FireTurnstyleEventRunnable fireEventCallable = new TurnstyleController.FireTurnstyleEventRunnable(lane, turnstyleController);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(waitForCallable);

        //sleep for more than timeout
        Thread.sleep(200);

        executor.submit(fireEventCallable);
        executor.shutdown();
        executor.awaitTermination(2000, TimeUnit.MILLISECONDS);

        Assert.assertFalse(waitForCallable.isSuccess());
        Assert.assertTrue(fireEventCallable.isSuccess());
    }
}
