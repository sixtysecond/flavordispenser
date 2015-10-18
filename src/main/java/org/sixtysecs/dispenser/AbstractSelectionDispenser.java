package org.sixtysecs.dispenser;

import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public abstract class AbstractSelectionDispenser<T, E> implements SelectionDispenser<T, E> {

    final Map<E, Queue<T>> inventory = new ConcurrentHashMap<E, Queue<T>>();

    /**
     * Ensure that an inventory collection exists for each E in newInventory
     *
     * @param selection the selection
     */
    protected void initSelectionInventory(E selection) {

        if (inventory.get(selection) != null) {
            return;
        }

        synchronized (selection) {
            if (inventory.get(selection) == null) {
                inventory.put(selection, new ConcurrentLinkedQueue<T>());
            }
        }

    }


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

        /**
         * Ensure that an inventory collection exists for each E in newInventory
         */
        for (Map.Entry<E, Collection<T>> entry : newInventory.entrySet()) {
            initSelectionInventory(entry.getKey());
        }
        for (Map.Entry<E, Collection<T>> entry : newInventory.entrySet()) {
            if (!CollectionUtils.isEmpty(entry.getValue())) {
                inventory.get(entry.getKey())
                        .addAll(entry.getValue());
            }
        }
    }


}