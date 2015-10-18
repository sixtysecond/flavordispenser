package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractSelfRefillingSelectionDispenser<T, E> extends AbstractSelectionDispenser<T, E> {
    protected SelectionFactory<T, E> selectionFactory;
    private Map<E, Integer> desiredInventory = new ConcurrentHashMap<E, Integer>();

    private AbstractSelfRefillingSelectionDispenser() {
        throw new UnsupportedOperationException("selectionFactory must be set in constructor");
    }

    public AbstractSelfRefillingSelectionDispenser(SelectionFactory<T, E> selectionFactory) {
        this.selectionFactory = selectionFactory;
    }

    protected Map<E, Integer> getDesiredInventory() {
        return desiredInventory;
    }

    /**
     * Sets desired inventory and attempts to fill inventory to desired inventory.
     *
     * @param desiredInventory
     */
    protected AbstractSelfRefillingSelectionDispenser setDesiredInventory(Map<E, Integer> desiredInventory) {
        if (desiredInventory == null) {
            desiredInventory = new ConcurrentHashMap<E, Integer>();
        }
        this.desiredInventory = desiredInventory;
        refillInventory();
        return this;
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
