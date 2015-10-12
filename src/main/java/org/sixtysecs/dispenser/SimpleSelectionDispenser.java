package org.sixtysecs.dispenser;

/**
 * Created by edriggs on 10/11/15.
 */
public class SimpleSelectionDispenser<T,E extends Enum<E>> extends AbstractSelectionDispenser<T,E> {

    public SimpleSelectionDispenser(Class tClass) {
        super(tClass);
    }
}
