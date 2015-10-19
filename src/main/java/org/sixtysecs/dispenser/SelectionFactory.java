package org.sixtysecs.dispenser;

/**
 * A factory for creating items to add to a SelectionDispenser's inventory.
 *
 * @param <T> the type of all objects instantiated
 * @param <E> which selection the objects should be added to. Enums are recommended.
 */
public interface SelectionFactory<T, E> {

    /**
     * Creates a single item
     * @param selection the selection to create the item for
     * @return an instance of T for use in selection E
     */
    public T createItem(E selection);
}
