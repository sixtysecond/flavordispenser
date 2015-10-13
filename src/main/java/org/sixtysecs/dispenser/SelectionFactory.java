package org.sixtysecs.dispenser;

import java.util.Collection;
import java.util.Map;

/**
 * A factory for creating objects of type T whose properties are determined by E.
 *
 * @param <T> the type of all objects instantiated
 * @param <E> an enumeration of selections deterining the properties of the instantiated object.
 */
public interface SelectionFactory<T, E extends Enum<E>> {

    /**
     * Fulfills an order for new inventory of objets
     * @param order the number of T to attempt to create for each E
     * @return a sparse map containing the T which were instantiated, grouped by selection E
     */
    public Map<E, Collection<T>> manufacture(Map<E, Integer> order);
}
