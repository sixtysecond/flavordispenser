package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSelfRefillingSelectionDispenser<T, E> extends AbstractSelectionDispenser<T, E> {
    protected final SelectionFactory<T, E> selectionFactory;
    protected volatile Map<E, Integer> desiredInventory = new ConcurrentHashMap<E, Integer>();

    private AbstractSelfRefillingSelectionDispenser() {
        throw new UnsupportedOperationException("selectionFactory must be set in constructor");
    }

    public AbstractSelfRefillingSelectionDispenser(SelectionFactory<T, E> selectionFactory) {
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
    protected AbstractSelfRefillingSelectionDispenser setDesiredInventory(Map<E, Integer> desiredInventory) {
        if (desiredInventory == null) {
            desiredInventory = new ConcurrentHashMap<E, Integer>();
        }
        this.desiredInventory = desiredInventory;
        refillInventory();
        return this;
    }

    @Override
    public Set<E> getSelections() {
        Set<E> selections = new HashSet<E>();
        for (E selection : inventory.keySet()) {
            selections.add(selection);
        }
        for (E selection : desiredInventory.keySet()) {
            selections.add(selection);
        }
        return selections;
    }



    @Override
    public T dispense(E selection) {

        initSelectionQueue(selection);

        T t = inventory.get(selection)
                .poll();
        if (t == null) {
            t = selectionFactory.createItem(selection);
        }
        refillSelection(selection);
        return t;
    }

    public void refillInventory() {
        for (E selection : getSelections()) {
            refillSelection(selection);
        }
    }

    public void refillSelection(E selection) {
        new RefillRunnable(selection).run();
    }

    protected class RefillRunnable implements Runnable {
        E selection;

        RefillRunnable(E selection) {
            this.selection = selection;
        }

        public void run() {
            initSelectionQueue(selection);
            synchronized (selection) {
                Queue<T> queue = inventory.get(selection);
                final int actualCount = queue.size();
                Integer desiredCount = desiredInventory.get(selection);
                if (desiredCount == null) {
                    desiredCount = 0;
                }
                final int diff = desiredCount - actualCount;

                /*Use order size of 1 since inventory is only
                available after entire order has been fulfilled*/
                for (int i = 0; i < diff; i++) {
                    Map<E, Integer> order = new ConcurrentHashMap<E, Integer>();
                    order.put(selection, 1);
                    addInventory(selectionFactory.fulfill(order));
                }
            }
        }
    }
}