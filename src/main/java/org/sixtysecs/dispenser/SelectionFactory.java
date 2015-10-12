package org.sixtysecs.dispenser;

import java.util.List;

/**
 * A selectionFactory instantiates objecs of type T using an enum E to define the instatiation parameters.
 * <p>
 * Each factory and its enumerator may have its own distinct set of properties
 * appropriate to the domain of instantiating that type of object.
 *
 * @author edriggs
 *
 * @param <T>
 *            the type instantiated
 * @param <E>
 *            the selection specifying the instantiation options
 */
public interface SelectionFactory<T, E extends Enum<E>> {

//    /**
//     * May return null. It is up to the factory's discretion whether to retry if
//     * the initial attempt to instantiate fails.
//     *
//     * @param selection
//     *            the selection which determines the instantiation properties
//     * @return a new instance of the associated type or <code>NULL</code>
//     */
//    public T newInstance(E selection);

    public List<T> create(E selection, int count);
}
