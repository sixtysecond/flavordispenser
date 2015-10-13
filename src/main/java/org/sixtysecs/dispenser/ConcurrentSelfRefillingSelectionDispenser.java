package org.sixtysecs.dispenser;

import java.util.Map;

/**
 * Created by edriggs on 10/11/15.
 */
public class ConcurrentSelfRefillingSelectionDispenser<T,E extends Enum<E>>  extends SelfRefillingSelectionDispenser<T,E>{


    public ConcurrentSelfRefillingSelectionDispenser(Class tClass, SelectionFactory<T, E> selectionFactory,
                                                     Map<E, Integer> desiredInventory) {
        super(tClass, selectionFactory, desiredInventory);
    }
}
