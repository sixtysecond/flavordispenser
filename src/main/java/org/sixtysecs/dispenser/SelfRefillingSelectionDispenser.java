package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A thread-safe dispenser which refills its inventory on instantiation and after dispensing a selection.
 *
 * @param <T>
 * @param <E>
 */
public class SelfRefillingSelectionDispenser<T, E extends Enum<E>> extends AbstractSelectionDispenser<T, E> {

    private SelectionFactory<T, E> selectionFactory;
    private Map<E, Integer> desiredInventory;

    public SelfRefillingSelectionDispenser(Class tClass, SelectionFactory<T, E> selectionFactory, Map<E, Integer> desiredInventory) {
        super(tClass);
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
            if (diff > 0) {
                addInventory(selection, selectionFactory.create(selection, diff));
            }
        }
    }

    public T dispense(E selection) {
        synchronized (selection) {
            refillSelection(selection);
            T t = inventory.get(selection)
                    .poll();
            refillSelection(selection);
            return t;
        }
    }
}
