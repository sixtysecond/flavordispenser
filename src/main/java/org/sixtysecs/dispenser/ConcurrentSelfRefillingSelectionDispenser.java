package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by edriggs on 10/11/15.
 */
public class ConcurrentSelfRefillingSelectionDispenser<T, E> extends AbstractSelectionDispenser<T, E> {

    protected ExecutorService executorService;
    protected SelectionFactory<T, E> selectionFactory;
    protected Map<E, Integer> desiredInventory;


    public ConcurrentSelfRefillingSelectionDispenser(SelectionFactory<T, E> selectionFactory, Map<E, Integer> desiredInventory,
                                                     int nThreads) {
        executorService = Executors.newFixedThreadPool(nThreads);
        this.selectionFactory = selectionFactory;
        this.desiredInventory = desiredInventory;
        for (E selection : desiredInventory.keySet()) {
            executorService.submit(new RefillRunnable(selection));
        }
    }

    @Override
    public T dispense(E selection) {
        T t = inventory.get(selection)
                .poll();
        if (t != null) {
            return t;
        }

        synchronized (selection) {
            t = inventory.get(selection)
                    .poll();

            if (t != null) {
                return t;
            }
            t = selectionFactory.createItem(selection);
            executorService.submit(new RefillRunnable(selection));
            return t;
        }
    }


    private class RefillRunnable implements Runnable {
        E selection;

        RefillRunnable(E selection) {
            this.selection = selection;
        }

        public void run() {
            synchronized (selection) {
                Queue<T> queue = inventory.get(selection);
                final int actualCount = queue.size();
                final int desiredCount = desiredInventory.get(selection);
                final int diff = desiredCount - actualCount;
                //Use order size of 1 since inventory is only available after entire order has been fulfilled
                for (int i = 0; i < diff; i++) {
                    Map<E, Integer> order = new ConcurrentHashMap<E, Integer>();
                    order.put(selection, 1);
                    addInventory(selectionFactory.fulfill(order));
                }
            }
        }
    }
}