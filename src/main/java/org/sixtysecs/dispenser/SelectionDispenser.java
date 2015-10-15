package org.sixtysecs.dispenser;

import java.util.*;

/**
 * Dispenses objects which share a type. Maintains an inventory of objects for each selection type,
 * and allows dispensing and adding inventory for each selection.
 *
 * @param <T> the type dispensed
 * @param <E> a selection determining which inventory to dispense from. Enums are recommended.
 * @author edriggs
 */
public interface SelectionDispenser<T, E> {

    /**
     * @return the set of selections the dispenser offers
     */
    public Set<E> getSelections();

    /**
     * @param selection Determines which inventory to dispense from. Similar to a product button on a vending machine.
     * @return an instance of T, or null if unable to dispense.
     */
    public T dispense(E selection);

    /**
     * @return the current inventory count for each selection
     */
    public Map<E, Integer> getInventoryCount();

    /**
     * @param newInventory adds the new inventory to the existing inventory.
     *                     for each E in the map, adds the collection of T to that selection's inventory
     */
    public void addInventory(Map<E, Collection<T>> newInventory);
}