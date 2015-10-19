package org.sixtysecs.dispenser;


import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by edriggs on 10/16/15.
 */
public class BackgroundThreadTest {

    @BeforeMethod
    public void nameBefore(Method method)
    {
        System.out.println("==== " +  getClass().getSimpleName() + "::" + method.getName() + " ====");
    }


    ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Test
    public void threadTest() {
        executorService.submit(new Thread() {
            public void run() {
                try {
                    new WaitForever();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private static class WaitForever {
        WaitForever() throws InterruptedException {
            Thread.sleep(9999999999l);
        }
    }
}
