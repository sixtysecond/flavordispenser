package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public abstract class AbstractSelectionDispenser<T, E> implements SelectionDispenser<T, E> {

    volatile Map<E, Queue<T>> inventory = new ConcurrentHashMap<E, Queue<T>>();

    public Set<E> getSelections() {
        Set<E> selections = new HashSet<E>();
        for (E selection : inventory.keySet()) {
            selections.add(selection);
        }
        return selections;
    }

    public int getSelectionInventoryCount(E selection) {
        Collection<T> selectionInventory = inventory.get(selection);
        if (selectionInventory == null) {
            return 0;
        }
        return selectionInventory.size();
    }

    public abstract T dispense(E selection);


    public final void addInventory(Map<E, Collection<T>> newInventory) {

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
