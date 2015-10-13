package org.sixtysecs.dispenser.turnstyle;

import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.*;

/**
 * Created by edriggs on 10/11/15.
 */
@Test(singleThreaded=true, threadPoolSize=1)
public class TurnstyleLaneEventTest {

    @BeforeMethod
    public void setup() {
        TurnstyleController.initEvents();
    }
    @Test(expectedExceptions = {TimeoutException.class})
    public void waitTimeouExceededTest() throws TimeoutException {
        TurnstyleEvent turnstyleLaneEvent = TurnstyleController.waitForEvent(TurnstyleLane.ONE, 0l);
    }

    @Test
    public void fireEventBeforeWaitTest() throws TimeoutException {
        final TurnstyleLane lane = TurnstyleLane.ONE;
        TurnstyleController.fireLaneEvent(lane);
        TurnstyleEvent event = TurnstyleController.waitForEvent(lane, 0l);
        Assert.assertNotNull(event);
        Assert.assertEquals(event.getTurnstyleLane(), lane);
    }

    @Test
    public void fireEventBeforeTimeoutTest() throws TimeoutException, InterruptedException {
        final TurnstyleLane lane = TurnstyleLane.ONE;

        TurnstyleController.WaitForCallable waitForCallable = new TurnstyleController.WaitForCallable(lane, 100);
        TurnstyleController.FireEventCallable fireEventCallable = new TurnstyleController.FireEventCallable(lane);
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

        final TurnstyleLane lane = TurnstyleLane.ONE;

        TurnstyleController.WaitForCallable waitForCallable = new TurnstyleController.WaitForCallable(lane, 100);
        TurnstyleController.FireEventCallable fireEventCallable = new TurnstyleController.FireEventCallable(lane);
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
