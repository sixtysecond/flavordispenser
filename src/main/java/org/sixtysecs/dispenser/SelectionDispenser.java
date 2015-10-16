package org.sixtysecs.dispenser;

import java.util.*;

/**
 * Has an inventory of items for each selection offerred, which may be dispensed from or added to. All items in all inventory share the same type.
 * Like a vending machine.
 *
 * @param <T> the type of all items in the inventories
 * @param <E> a selection determining which inventory to dispense from or add to. Enums are recommended.
 * @author edriggs
 */
public interface SelectionDispenser<T, E> {

    /**
     * @return the set of selections the dispenser offers
     */
    public Set<E> getSelections();

    /**
     * @return the current inventory count for each selection
     */
    public Map<E, Integer> getInventoryCount();


    /**
     * @param selection Determines which inventory to dispense from. Similar to a product button on a vending machine.
     * @return an instance of T, or null if unable to dispense.
     */
    public T dispense(E selection);

    
    /**
     * @param newInventory adds the new inventory to the existing inventory.
     *                     for each E in the map, adds the collection of T to that selection's inventory
     */
    public void addInventory(Map<E, Collection<T>> newInventory);
}