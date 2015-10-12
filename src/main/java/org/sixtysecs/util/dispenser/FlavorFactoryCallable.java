package org.sixtysecs.util.dispenser;

import java.util.concurrent.Callable;


/**
 * A callable which composes an {@link FlavorFactory} in order to support
 * concurrent instantiation of flavors.
 *
 * @author edriggs
 */
class FlavorFactoryCallable<T, E extends Enum<E>> implements Callable<T> {

    private final FlavorFactory<T, E> flavorFactory;
    private final E flavor;

    /**
     * @param flavorFactory the factory which is able to generate instances for each
     *                      flavor
     * @param flavor        the flavor to instantiate
     */
    FlavorFactoryCallable(FlavorFactory<T, E> flavorFactory, E flavor) {
        if (flavorFactory == null) {
            throw new NullPointerException("flavorFactory");
        }
        if (flavor == null) {
            throw new NullPointerException("flavor");
        }

        this.flavorFactory = flavorFactory;
        this.flavor = flavor;
    }

    ;

    /**
     * @returns an instance from the factory . May be null.
     */
    public T call()  {
        return flavorFactory.newInstance(flavor);
    }
}
