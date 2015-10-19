package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.*;

public class SelfRefillingSelectionDispenser<T, E> extends AbstractSelectionDispenser<T, E> {
    protected final SelectionFactory<T, E> selectionFactory;
    protected volatile Map<E, Integer> desiredInventory = new ConcurrentHashMap<E, Integer>();
    protected ExecutorService executorService = Executors.newCachedThreadPool();
    protected TimeUnit refillWaitTimeUnit;
    protected long refillWaitCount;

    private SelfRefillingSelectionDispenser() {
        throw new UnsupportedOperationException("selectionFactory must be set in constructor");
    }

    public SelfRefillingSelectionDispenser(SelectionFactory<T, E> selectionFactory) {
        this.selectionFactory = selectionFactory;
    }

    protected Map<E, Integer> getDesiredInventory() {
        return desiredInventory;
    }

    /**
     * Sets desired inventory and attempts to fill inventory to desired inventory.
     *
     * @param desiredInventory
     */
    protected SelfRefillingSelectionDispenser setDesiredInventory(Map<E, Integer> desiredInventory) {
        if (desiredInventory == null) {
            desiredInventory = new ConcurrentHashMap<E, Integer>();
        }
        this.desiredInventory = desiredInventory;
        refillInventory();
        return this;
    }

    public void setRefillTimeOut(TimeUnit timeUnit, long count) {
        this.refillWaitCount = 1;
        this.refillWaitTimeUnit = TimeUnit.MINUTES;
    }

    public TimeUnit getRefillWaitTimeUnit() {
        return refillWaitTimeUnit;
    }

    public long getRefillWaitCount() {
        return refillWaitCount;
    }

    @Override
    public Set<E> getSelections() {
        Set<E> selections = super.getSelections();
        for (E selection : desiredInventory.keySet()) {
            selections.add(selection);
        }
        return selections;
    }

    @Override
    public T dispense(E selection) {

        initSelectionInventory(selection);
        T t = inventory.get(selection)
                .poll();
        if (t == null) {
            t = selectionFactory.createItem(selection);
        }
        refillSelection(selection);
        return t;
    }

    public void createInventory(E selection, int count) {
        executorService.submit(new AddItemsRunnable(selection, count));
    }

    protected void refillInventory() {
        for (E selection : getSelections()) {
            refillSelection(selection);
        }
    }

    protected void refillSelection(E selection) {
        executorService.submit(new RefillSelectionCallable(selection));
    }


    protected class RefillSelectionCallable implements Callable {
        E selection;

        RefillSelectionCallable(E selection) {
            this.selection = selection;
        }

        public Boolean call() throws InterruptedException {
            initSelectionInventory(selection);

            synchronized (selection) {
                ExecutorService refillExecutorService = Executors.newCachedThreadPool();
                Queue<T> queue = inventory.get(selection);
                final int actualCount = queue.size();
                Integer desiredCount = desiredInventory.get(selection);
                if (desiredCount == null) {
                    desiredCount = 0;
                }
                final int diff = desiredCount - actualCount;

                for (int i = 0; i < diff; i++) {
                    refillExecutorService.submit(new RefillSelectionItemRunnable(selection));
                }
                refillExecutorService.shutdown();
                refillExecutorService.awaitTermination(refillWaitCount, refillWaitTimeUnit);
            }
            return true;
        }

    }

    protected class RefillSelectionItemRunnable implements Runnable {
        E selection;

        RefillSelectionItemRunnable(E selection) {
            this.selection = selection;
        }

        public void run() {
            initSelectionInventory(selection);
            Queue<T> queue = inventory.get(selection);
            final int actualCount = queue.size();
            Integer desiredCount = desiredInventory.get(selection);
            if (desiredCount == null) {
                desiredCount = 0;
            }
            if (desiredCount > actualCount) {
                executorService.submit(new AddItemRunnable(selection));
            }

        }

    }

    protected class AddItemsRunnable implements Runnable {
        E selection;
        int count;

        AddItemsRunnable(E selection, int count) {
            this.selection = selection;
            this.count = count;
        }

        public void run() {
            for (int i = 0; i < count; i++) {
                executorService.submit(new AddItemRunnable(selection));
            }
        }
    }

    protected class AddItemRunnable implements Runnable {
        E selection;

        AddItemRunnable(E selection) {
            this.selection = selection;
        }

        public void run() {
            addInventory(selection, selectionFactory.createItem(selection));
        }
    }
}