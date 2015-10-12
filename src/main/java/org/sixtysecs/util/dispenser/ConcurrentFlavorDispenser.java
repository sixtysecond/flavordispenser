package org.sixtysecs.util.dispenser;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A thread-safe implementation of {@link FlavorDispenser} which refills using background threads as it dispenses.
 * Can only be instantiated through {@link ConcurrentFlavorDispenser.FlavorDispenserBuilder}.
 * <p/>
 * If the desiredInventory is not specified or is set to below 1 for any flavor, the desired inventory for that flavor is set to 1.
 * <p/>
 * Only the following properties have mutable state:
 * <ul>
 * <li>flavorInventory</li>
 * <li>flavorDispensedCountMap</li>
 * </ul>
 * {@see FlavorDispenser}
 *
 * @param <T> the type instantiated
 * @param <E> the flavor specifying the instantiation options
 */
public class ConcurrentFlavorDispenser<T, E extends Enum<E>> extends AbstractDispenser<T, E> implements
        FlavorDispenser<T, E> {

    private final int threadCount;
    private final int timeoutMinutes;

    /**
     * Can only be instantiated from builder
     *
     * @param builder
     */
    private ConcurrentFlavorDispenser(FlavorDispenserBuilder<T, E> builder) {
        super(builder.desiredInventory, builder.flavorFactory);
        this.threadCount = builder.nThreads;
        this.timeoutMinutes = builder.timeoutMinutes;
        for (E flavor : flavorFactory.getAllFlavors()) {
            ConcurrentLinkedQueue<T> concurrentLinkedQueue = new ConcurrentLinkedQueue<T>();
            flavorInventory.put(flavor, concurrentLinkedQueue);
            flavorDispensedCountMap.put(flavor, new AtomicInteger(0));
        }
        backgroundRefillAll();
    }


    public T dispense(E flavor) {
        flavorDispensedCountMap.get(flavor)
                .incrementAndGet();
        logger.debug("dispenseCountMap=" + flavorDispensedCountMap);
        ConcurrentLinkedQueue<T> flavorQueue = flavorInventory.get(flavor);

        synchronized (flavor) {
            T instance = flavorQueue.poll();

            if (instance == null) {
                addStockToInventory(fulfillOrder(calculateOrder(flavor)));
                instance = flavorQueue.poll();
                if (instance == null) {
                    throw new NullPointerException("Unable to dispense: "
                            + flavor.name());
                }
            }
            backgroundRefill(flavor);
            return instance;
        }

    }

    public Map<E, List<T>> fulfillOrder(final Map<E, Integer> order) {
        return new FlavorFactoryExecutor<T, E>(flavorFactory,
                                order, threadCount, timeoutMinutes).execute();
    }

    public void backgroundRefillAll() {
        for ( E flavor : flavorFactory.getAllFlavors()) {
            Map<E, Integer> order = calculateOrder(flavor);
            addStockToInventory(new FlavorFactoryExecutor<T, E>(flavorFactory,
                    order, threadCount, timeoutMinutes).execute());
        }
    }

    public void backgroundRefill(E flavor) {
        Map<E, Integer> order = calculateOrder(flavor);
        addStockToInventory(new FlavorFactoryExecutor<T, E>(flavorFactory,
                order, threadCount, timeoutMinutes).execute());
    }


    /**
     * A mutable builder for instantiating FlavorDispensers
     * <p/>
     * flavorFactory is a required constructor parameter and cannot be
     * <code>NULL</code>.
     * <p/>
     * All other parameters are optional, are accessible through a fluent
     * interface of setters, and have the following default behavior:
     * <ul>
     * <li>Collections default to empty</li>
     * <li>booleans default to true</li>
     * <li>Integers cannot be set below 1. Default values are specified on
     * setter contracts.</li>
     * </ul>
     *
     * @param <T> the type instantiated
     * @param <E> the flavor specifying the instantiation options
     * @author edriggs
     */
    public static class FlavorDispenserBuilder<T, E extends Enum<E>> {

        private FlavorFactory<T, E> flavorFactory;
        private ConcurrentHashMap<E, Integer> desiredInventory;
        private Integer nThreads;
        private Integer timeoutMinutes;

        /**
         * @param flavorFactory . The flavor factory to use. Cannot be <code>NULL<code>.
         */
        public FlavorDispenserBuilder(FlavorFactory<T, E> flavorFactory) {
            if (flavorFactory == null) {
                throw new NullPointerException("flavorFactory");
            }
            this.flavorFactory = flavorFactory;
        }

        /**
         * Optional, defaults to empty initial inventory.
         *
         * @param desiredInventory
         */
        public FlavorDispenserBuilder<T, E> setDesiredlInventory(
                Map<E, Integer> desiredInventory) {
            this.desiredInventory = new ConcurrentHashMap<E, Integer>(desiredInventory);
            return this;
        }


        /**
         * @param flavor
         * @param desiredCount
         * @return
         */
        public FlavorDispenserBuilder<T, E> setDesiredlFlavorInventory(
                E flavor, int desiredCount) {
            if (desiredInventory == null) {
                desiredInventory = new ConcurrentHashMap<E, Integer>();
            }
            desiredInventory.put(flavor, desiredCount);
            return this;
        }

        /**
         * Optional. Defaults to 20. Values below 1 are ignored.
         *
         * @param threadCount the number of threads to use when refilling
         * @return
         */
        public FlavorDispenserBuilder<T, E> setThreadCount(Integer threadCount) {
            this.nThreads = threadCount;
            return this;
        }

        /**
         * Optional. Defaults to 60. Values below 1 are ignored.
         *
         * @param timeoutMinutes
         * @return
         */
        public FlavorDispenserBuilder<T, E> setTimeoutMinutes(
                Integer timeoutMinutes) {
            this.timeoutMinutes = timeoutMinutes;
            return this;
        }

        /**
         * @return a FlavorDispenser
         */
        public FlavorDispenser<T, E> build() {
            if (flavorFactory == null) {
                throw new NullPointerException("flavorFactory");
            }
            if (desiredInventory == null) {
                desiredInventory = new ConcurrentHashMap<E, Integer>();
            }
            for (E flavor : flavorFactory.getAllFlavors()) {
                if (desiredInventory.get(flavor) == null || desiredInventory.get(flavor)  < 1) {
                    desiredInventory.put(flavor, 1);
                }
            }
            if (nThreads == null || nThreads < 1) {
                nThreads = 20;
            }
            if (timeoutMinutes == null || timeoutMinutes < 1) {
                timeoutMinutes = 60;
            }
            return new ConcurrentFlavorDispenser<T, E>(this);
        }

    }
}
