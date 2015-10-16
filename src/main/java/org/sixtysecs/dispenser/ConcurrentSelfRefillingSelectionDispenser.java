package org.sixtysecs.dispenser;

import java.util.Collection;
import java.util.Map;

/**
 * Created by edriggs on 10/11/15.
 */
public class ConcurrentSelfRefillingSelectionDispenser<T, E> extends AbstractSelectionDispenser<T, E> {

    @Override
    public T dispense(E selection) {
        return null;
    }

    @Override
    public void addInventory(Map<E, Collection<T>> newInventory) {

    }
}
