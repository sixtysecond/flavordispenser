package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A thread-safe dispenser which refills its inventory on instantiation and after dispensing a selection.
 *
 * @param <T>
 * @param <E>
 */
public class SelfRefillingSelectionDispenser<T, E> extends AbstractSelectionDispenser<T, E> {

    protected SelectionFactory<T, E> selectionFactory;
    protected Map<E, Integer> desiredInventory;

    public SelfRefillingSelectionDispenser( SelectionFactory<T, E> selectionFactory, Map<E, Integer> desiredInventory) {
        this.selectionFactory = selectionFactory;
        this.desiredInventory = sanitizeDesiredInventory(desiredInventory);
        refillInventory();
    }

    public Map<E, Integer> sanitizeDesiredInventory(Map<E, Integer> desiredInventory) {
        if (desiredInventory == null) {
            desiredInventory = new ConcurrentHashMap<E, Integer>();
        }
        for (E selection : getSelections()) {
            Integer desiredSelectionCount = desiredInventory.get(selection);
            if (desiredSelectionCount == null) {
                desiredInventory.put(selection, 1);
            }
        }
        return desiredInventory;
    }

    public void refillInventory() {
        for (E selection : getSelections()) {
            refillSelection(selection);
        }
    }


    public void refillSelection(E selection) {
        synchronized (selection) {
            Queue<T> queue = inventory.get(selection);
            final int actualCount = queue.size();
            final int desiredCount = desiredInventory.get(selection);
            final int diff = desiredCount - actualCount;
            Map<E,Integer> order = new ConcurrentHashMap<E, Integer>();
            order.put(selection, diff);
            if (diff > 0) {
                addInventory( selectionFactory.fulfill(order));
            }
        }
    }

    @Override
    public T dispense(E selection) {
        synchronized (selection) {
            refillSelection(selection);
            T t = inventory.get(selection)
                    .poll();
            refillSelection(selection);
            return t;
        }
    }

    @Override
    public void addInventory(Map<E, Collection<T>> newInventory) {
        synchronized (this) {
            for (Map.Entry<E, Collection<T>> entry : newInventory.entrySet()) {
                Queue<T> selectionInventory = inventory.get(entry.getKey());
                if (selectionInventory == null) {
                    newInventory.put(entry.getKey(), new ConcurrentLinkedQueue<T>());
                }
                selectionInventory.addAll(entry.getValue());
            }
        }
    }
}
