package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by edriggs on 10/16/15.
 */
public abstract class AbstractSelfRefillingSelectionDispenser<T, E> extends AbstractSelectionDispenser<T, E> {
    protected SelectionFactory<T, E> selectionFactory;
    private Map<E, Integer> desiredInventory;

    protected AbstractSelfRefillingSelectionDispenser() {
    }

    public AbstractSelfRefillingSelectionDispenser(SelectionFactory<T, E> selectionFactory,
                                                   Map<E, Integer> desiredInventory) {
        this.selectionFactory = selectionFactory;
        setDesiredInventory(desiredInventory);
        refillInventory();
    }

    protected void setDesiredInventory(Map<E, Integer> desiredInventory) {
        if (desiredInventory == null) {
            desiredInventory = new ConcurrentHashMap<E, Integer>();
        }
        this.desiredInventory = desiredInventory;
    }

    protected Map<E, Integer> getDesiredInventory() {
        return desiredInventory;
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
