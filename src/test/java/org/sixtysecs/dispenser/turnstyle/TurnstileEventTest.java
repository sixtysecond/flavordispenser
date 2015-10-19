package org.sixtysecs.dispenser.turnstyle;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * Created by edriggs on 10/11/15.
 */
@Test(singleThreaded=true, threadPoolSize=1)
public class TurnstileEventTest {

    @BeforeMethod
    public void nameBefore(Method method)
    {
        System.out.println("==== " +  getClass().getSimpleName() + "::" + method.getName() + " ====");
    }

    @Test(expectedExceptions = {TimeoutException.class})
    public void waitTimeouExceededTest() throws TimeoutException {
        TurnstileController turnstyleController = new TurnstileController();
        TurnstileEvent turnstyleLaneEvent = turnstyleController.waitForEvent(TurnstileLane.ONE, 0l);
    }

    @Test
    public void fireEventBeforeWaitTest() throws TimeoutException {
        TurnstileController turnstyleController = new TurnstileController();
        final TurnstileLane lane = TurnstileLane.ONE;
        turnstyleController.fireLaneEvent(lane);
        TurnstileEvent event = turnstyleController.waitForEvent(lane, 0l);
        Assert.assertNotNull(event);
        Assert.assertEquals(event.getLane(), lane);
    }

    @Test
    public void fireEventBeforeTimeoutTest() throws TimeoutException, InterruptedException {
        TurnstileController turnstyleController = new TurnstileController();
        final TurnstileLane lane = TurnstileLane.ONE;

        WaitForTurnstyleEventCallable waitForCallable =
                new WaitForTurnstyleEventCallable(lane, turnstyleController, 100);
        FireTurnstyleEventRunnable fireEventRunnable = new FireTurnstyleEventRunnable(lane, turnstyleController);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(waitForCallable);

        //sleep for less than timeout
        Thread.sleep(20);

        executor.submit(fireEventRunnable);
        executor.shutdown();
        executor.awaitTermination(2000, TimeUnit.MILLISECONDS);
        Assert.assertTrue(waitForCallable.isSuccess());
        Assert.assertTrue(fireEventRunnable.isSuccess());
    }

    @Test
    public void fireEventAfterTimeoutTest() throws TimeoutException, InterruptedException {
        TurnstileController turnstyleController = new TurnstileController();
        final TurnstileLane lane = TurnstileLane.ONE;

        WaitForTurnstyleEventCallable waitForCallable = new WaitForTurnstyleEventCallable(lane, turnstyleController, 100);
        FireTurnstyleEventRunnable fireEventRunnable = new FireTurnstyleEventRunnable(lane, turnstyleController);
        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(waitForCallable);

        //sleep for more than timeout
        Thread.sleep(200);

        executor.submit(fireEventRunnable);
        executor.shutdown();
        executor.awaitTermination(2000, TimeUnit.MILLISECONDS);

        Assert.assertFalse(waitForCallable.isSuccess());
        Assert.assertTrue(fireEventRunnable.isSuccess());
    }
}
