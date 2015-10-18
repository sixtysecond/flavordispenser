package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A thread-safe, blocking dispenser which refills its inventory on instantiation and after dispensing a selection.
 *
 * @param <T>
 * @param <E>
 */
public class BlockingSelfRefillingSelectionDispenser<T, E> extends AbstractSelfRefillingSelectionDispenser<T, E> {


    public BlockingSelfRefillingSelectionDispenser(SelectionFactory<T, E> selectionFactory) {
        super(selectionFactory);
    }

    @Override
    public void refillInventory() {
        for (E selection : getSelections()) {
            refillSelection(selection);
        }
    }

    public void refillSelection(E selection) {
        synchronized (selection) {
            final int inventoryCount = getSelectionInventoryCount(selection);
            Integer desiredCount = getDesiredInventory().get(selection);
            if (desiredCount == null) {
                desiredCount = 0;
            }
            final int diff = desiredCount - inventoryCount;
            Map<E, Integer> order = new ConcurrentHashMap<E, Integer>();
            order.put(selection, diff);
            if (diff > 0) {
                addInventory(selectionFactory.fulfill(order));
            }
        }
    }

    @Override
    public T dispense(E selection) {
        synchronized (selection) {
            refillSelection(selection);
            Queue<T> inventorySelection = inventory.get(selection);
            if (inventorySelection == null) {
                inventory.put(selection, new ConcurrentLinkedQueue<T>());
                inventorySelection = inventory.get(selection);
            }

            T t = inventorySelection.poll();
            if (t == null) {
                Map<E, Integer> order = new ConcurrentHashMap<E, Integer>();
                order.put(selection, 1);
                Map<E, Collection<T>> newInventory = selectionFactory.fulfill(order);
            }
            refillSelection(selection);
            return t;
        }
    }


}
