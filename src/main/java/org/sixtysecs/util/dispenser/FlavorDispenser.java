package org.sixtysecs.util.dispenser;

import java.util.Map;

/**
 * A dispenser provides a factory-like interface for objects of a single type.
 * Dispensers are useful when instantiation of a type is time-consuming but may be parallelized
 * and whose instantiation parameters can be defined in an enumeration.
 * The objects dispensed may be created on demand, or they may have been created previously.
 * The instantiation parameters are determined by an enumeration (flavor).
 * <p/>
 * Dispensers maintain an inventory of instances for each flavor. Dispensers can
 * be configured to refill in the background if a flavor's inventory is below
 * the background refill threshold.
 * <p/>
 * During instantiation, a dispenser should attempt to fill itself based on the
 * value of {@link FlavorDispenser#getInitialInventoryCount}.
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
     * @return how many instances to add to the dispenser for an flavor when a
     * dispenser is asked to dispense a flavor which is it empty of.
     */
    public int getRefillAmount();

    /**
     * @return whether the dispenser should attempt to refill flavors in the
     * background when those flavors are running low.
     */
    public boolean isBackgroundRefill();

    /**
     * @return the threshold at which the dispenser will attempt to refill
     * flavors in the background.
     */
    public int getBackgroundRefillThreshold();

    /**
     * @return the number of instances to attempt to refill in the background
     */
    public int getBackgroundRefillAmount();

    /**
     * @return the initial flavors which the dispenser will attempt to fill
     * itself with on instantiation
     */
    public Map<E, Integer> getInitialInventoryCount();

}
