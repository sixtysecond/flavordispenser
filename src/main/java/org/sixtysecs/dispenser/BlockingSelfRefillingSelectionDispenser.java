package org.sixtysecs.dispenser;

/**
 * A thread-safe, blocking dispenser which refills its inventory on instantiation and after dispensing a selection.
 *
 * @param <T>
 * @param <E>
 */
public class BlockingSelfRefillingSelectionDispenser<T, E> extends AbstractSelfRefillingSelectionDispenser<T, E> {


    public BlockingSelfRefillingSelectionDispenser(SelectionFactory<T, E> selectionFactory) {
        super(selectionFactory);
    }

}
