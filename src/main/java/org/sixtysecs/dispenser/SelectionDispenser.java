package org.sixtysecs.dispenser;

import java.util.*;

/**
 * Dispenses objects of a single type whose properties are determined by the selection made.
 * A vending machine for objects.
 *
 * @param <T> the type dispensed
 * @param <E> an enumeration of selections. The selection determines the properties of
 *            the object dispensed and an inventory exists for each selection.
 * @author edriggs
 */
public interface SelectionDispenser<T, E extends Enum<E>> {

    /**
     * @return the set of enumerated selections the dispenser supports
     */
    public Set<E> getSelections();

    /**
     * @param selection an enumerated member which has its own inventory and determines the properties of the dispensed item.
     *                  Similar to a product button on a vending machine.
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