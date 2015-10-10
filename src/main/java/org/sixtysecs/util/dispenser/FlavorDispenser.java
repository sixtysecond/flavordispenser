package org.sixtysecs.util.dispenser;

import java.util.Map;

/**
 * A dispenser provides a factory-like interface for objects of a single type.
 * Dispensers are useful when instantiation of a type is time-consuming but may be parallelized
 * and whose instantiation parameters can be defined in an enumeration.
 * The objects dispensed may be created on demand, or they may have been created previously.
 * The instantiation parameters are determined by an enumeration (flavor).
 * <p/>
 * Dispensers may maintain an inventory of instances for each flavor, which may be refreshed either
 * synchronously or in the background.
 * <p/>
 * After instantiation, a dispenser should attempt to fill itself based on the
 * value of {@link FlavorDispenser#getDesiredInventory}.
 *
 * @param <T> the type instantiated
 * @param <E> the flavor specifying the instantiation options
 * @author edriggs
 */
public interface FlavorDispenser<T, E extends Enum<E>> {

    /**
     * @param flavor the flavor to dispense.
     * @return an instance, or null if unable to dispense. Should not throw.
     */
    public T dispense(E flavor);

    /**
     * @param flavor
     * @return the count of the current inventory for the requested flavor
     */
    public int getInventoryCount(E flavor);

    /**
     * @return the initial flavors which the dispenser will attempt to fill
     * itself with on instantiation
     */
    public Map<E, Integer> getDesiredInventory();

}
