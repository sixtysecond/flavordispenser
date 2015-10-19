package org.sixtysecs.dispenser;


/**
 * Created by edriggs on 10/16/15.
 */
public abstract class AbstractSelectionFactory<T, E> implements SelectionFactory<T, E> {

    public abstract T createItem(E selection);

}
