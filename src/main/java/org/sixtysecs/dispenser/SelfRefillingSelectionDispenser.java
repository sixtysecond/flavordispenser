package org.sixtysecs.dispenser;

import java.util.Arrays;
import java.util.Queue;

/**
 * Created by edriggs on 10/11/15.
 */
public class SelfRefillingSelectionDispenser<T,E extends Enum<E>> extends AbstractSelectionDispenser<T,E> {

    private SelectionFactory<T,E> selectionFactory;
    public SelfRefillingSelectionDispenser(Class tClass, SelectionFactory<T,E> selectionFactory) {
        super(tClass);
        this.selectionFactory = selectionFactory;
    }

    public T dispense(E selection) {
        synchronized(selection) {
            Queue<T> queue = inventory.get(selection);
            T item = queue.poll();
            if (item == null) {
                addInventory(selection, Arrays.asList(selectionFactory.newInstance(selection)));
            }
            return queue.poll();
        }
    }
}
