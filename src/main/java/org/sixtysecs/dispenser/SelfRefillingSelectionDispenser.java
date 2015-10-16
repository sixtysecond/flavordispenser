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

    public SelfRefillingSelectionDispenser(SelectionFactory<T, E> selectionFactory, Map<E, Integer> desiredInventory) {
        this.selectionFactory = selectionFactory;
        if (desiredInventory == null) {
            desiredInventory = new ConcurrentHashMap<E, Integer>();
        }
        this.desiredInventory = desiredInventory;
        refillInventory();
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

    public void refillInventory() {
        for (E selection : getSelections()) {
            refillSelection(selection);
        }
    }


    public void refillSelection(E selection) {
        synchronized (selection) {
            final int inventoryCount = getSelectionInventoryCount(selection);
            Integer desiredCount = desiredInventory.get(selection);
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

    @Override
    public void addInventory(Map<E, Collection<T>> newInventory) {

            for (Map.Entry<E, Collection<T>> entry : newInventory.entrySet()) {
                synchronized(entry.getKey()) {
                    Queue<T> selectionInventory = inventory.get(entry.getKey());
                    if (selectionInventory == null) {
                        inventory.put(entry.getKey(), new ConcurrentLinkedQueue<T>());
                    }
                    inventory.get(entry.getKey())
                            .addAll(entry.getValue());
                }
            }

    }
}
