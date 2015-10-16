package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by edriggs on 10/16/15.
 */
public abstract class AbstractSelfRefillingSelectionDispenser<T, E> extends AbstractSelectionDispenser<T, E> {
    protected SelectionFactory<T, E> selectionFactory;
    protected Map<E, Integer> desiredInventory;

    public AbstractSelfRefillingSelectionDispenser(SelectionFactory<T, E> selectionFactory,
                                                   Map<E, Integer> desiredInventory) {
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

    public abstract void refillInventory();

}
