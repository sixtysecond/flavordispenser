package org.sixtysecs.dispenser;

import java.util.*;
import java.util.concurrent.*;

/**
 * Created by edriggs on 10/11/15.
 */
public class ConcurrentSelfRefillingSelectionDispenser<T, E> extends AbstractSelfRefillingSelectionDispenser<T, E> {

    protected ExecutorService executorService = Executors.newCachedThreadPool();
    protected SelectionFactory<T, E> selectionFactory;
    protected Map<E, Integer> desiredInventory;


    public ConcurrentSelfRefillingSelectionDispenser(SelectionFactory<T, E> selectionFactory) {
        super(selectionFactory);
    }

    public void refillSelection(E selection) {
        executorService.submit(new RefillRunnable(selection));
    }
}