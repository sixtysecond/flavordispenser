package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public abstract class AbstractSelectionDispenser<T, E> implements SelectionDispenser<T, E> {

    volatile Map<E, Queue<T>> inventory = new ConcurrentHashMap<E, Queue<T>>();

    public Set<E> getSelections() {
        Set<E> selections = new HashSet<E>();
        for (E selection : inventory.keySet()) {
            selections.add(selection);
        }
        return selections;
    }

    //    public Map<E, Integer> getInventoryCount() {
    //        Map<E, Integer> inventoryCount = new HashMap<E, Integer>();
    //        for (E selection : getSelections()) {
    //            Integer count = inventory.get(selection)
    //                    .size();
    //            if (count == null) {
    //                count = 0;
    //            }
    //            inventoryCount.put(selection, count);
    //        }
    //        if (inventoryCount == null) {
    //
    //        }
    //        return inventoryCount;
    //    }

    public int getSelectionInventoryCount(E selection) {
        Collection<T> selectionInventory = inventory.get(selection);
        if (selectionInventory == null) {
            return 0;
        }
        return selectionInventory.size();
    }

    public abstract T dispense(E selection);


    public abstract void addInventory(Map<E, Collection<T>> newInventory);


}
