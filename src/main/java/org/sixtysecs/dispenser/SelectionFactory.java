package org.sixtysecs.dispenser;

import java.util.Collection;
import java.util.Map;

/**
 * A factory for creating items to add to a SelectionDispenser's inventory.
 *
 * @param <T> the type of all objects instantiated
 * @param <E> which selection the objects should be added to. Enums are recommended.
 */
public interface SelectionFactory<T, E> {

    /**
     * Fulfills an order for new inventory of objets
     * @param order the number of T to attempt to create for each E
     * @return a sparse map containing the T which were instantiated, grouped by selection E
     */
    public Map<E, Collection<T>> fulfill(Map<E, Integer> order);

    public T createItem(E selection);
}
