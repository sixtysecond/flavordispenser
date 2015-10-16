package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A thread-safe blocking dispenser
 *
 * @see SelectionDispenser
 */
public class ConcurrentSelectionDispenser<T, E> extends AbstractSelectionDispenser<T, E> {
    Map<E, Queue<T>> inventory = new ConcurrentHashMap<E, Queue<T>>();


    public T dispense(E selection) {
        Queue<T> queue = inventory.get(selection);
        if (queue == null) {
            return null;
        }
        return queue.poll();
    }

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
