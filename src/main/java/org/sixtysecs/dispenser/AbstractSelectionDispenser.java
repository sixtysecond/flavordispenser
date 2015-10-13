package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class AbstractSelectionDispenser<T, E extends Enum<E>>
        implements SelectionDispenser<T, E> {

    Map<E, Queue<T>> inventory = new ConcurrentHashMap<E, Queue<T>>();
    private Class tClass;

    private AbstractSelectionDispenser() {
        throw new IllegalStateException("Need to know selection class");
    }

    public AbstractSelectionDispenser(Class tClass) {
        this.tClass = tClass;
        getSelections();
        for (E selection : getSelections()) {
            inventory.put(selection, new ConcurrentLinkedQueue<T>());
        }
    }

    public T dispense(E selection) {
        Queue<T> queue = inventory.get(selection);
        return queue.poll();
    }

    public Map<E, Integer> getInventoryCount() {
        Map<E, Integer> inventoryCount = new HashMap<E, Integer>();
        for (E selection : getSelections()) {
            inventoryCount.put(selection, inventory.get(selection)
                    .size());
        }
        return inventoryCount;
    }

    public int getSelectionInventoryCount(E selection) {
        Map<E, Integer> inventoryCount = new HashMap<E, Integer>();
        Integer count = inventory.get(selection)
                .size();
        if (count == null) {
            count = 0;
        }
        return count;
    }


    public void addInventory(Map<E, Collection<T>> newInventory) {
        for (Map.Entry<E, Collection<T>> entry : newInventory.entrySet()) {
            Queue<T> selectionInventory = inventory.get(entry.getKey());
            selectionInventory.addAll(entry.getValue());
        }
    }

    public Set<E> getSelections() {
        return EnumSet.allOf(tClass);
    }
}
