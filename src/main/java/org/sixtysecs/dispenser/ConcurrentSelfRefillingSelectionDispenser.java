package org.sixtysecs.dispenser;

/**
 * Created by edriggs on 10/11/15.
 */
public class ConcurrentSelfRefillingSelectionDispenser<T,E extends Enum<E>>  extends AbstractSelectionDispenser<T,E>{
    public ConcurrentSelfRefillingSelectionDispenser(Class tClass) {
        super(tClass);
    }


}
