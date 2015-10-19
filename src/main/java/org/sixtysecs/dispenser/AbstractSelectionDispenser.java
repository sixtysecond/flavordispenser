package org.sixtysecs.dispenser;

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

    /**
     * Note: Must call initSelectionInventory before dispensing to prevent null pointer
     * @param selection Determines which inventory to dispense from. Similar to a product button on a vending machine.
     * @return
     */
    public abstract T dispense(E selection);


    public final void addInventory(E selection, T newItem) {

        initSelectionInventory(selection);
        inventory.get(selection)
                .add(newItem);
    }
}
