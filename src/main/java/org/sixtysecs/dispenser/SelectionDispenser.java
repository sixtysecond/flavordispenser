package org.sixtysecs.dispenser;

import java.util.*;

/**
 * A selection dispenser is like a vending machine for objects.
 * All objects dispensed are the same type,
 * but their properties are determined by which selection was dispensed.
 *
 * @param <T> the type instantiated
 * @param <E> an enumeration of selections
 * @author edriggs
 */
public interface SelectionDispenser<T, E extends Enum<E>> {

    /**
     * @param selection the selection to dispense.
     * @return an instance, or null if unable to dispense.
     */
    public T dispense(E selection);

    /**
     * @return the initial flavors which the dispenser will attempt to fill
     * itself with on instantiation
     */
    public Map<E, Integer> getInventoryCount();

    /**
     *
     * @param selection the selection to put the instances into
     * @param newInventory the new instances to add to the dispenser
     */
    public void addInventory(E selection, Collection<T> newInventory);

    public Set<E> getSelections();
}