package org.sixtysecs.util.dispenser;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;
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
public class ConcurrentFlavorDispenser<T, E extends Enum<E>> implements
        FlavorDispenser<T, E> {

    protected final Logger logger = Logger.getLogger(ConcurrentFlavorDispenser.class);

    private final int threadCount;
    private final int timeoutMinutes;

    private final Map<E, Integer> desiredInventory;
    private final FlavorFactory<T, E> flavorFactory;

    private final Map<E, ConcurrentLinkedQueue<T>> flavorInventory = new ConcurrentHashMap<E, ConcurrentLinkedQueue<T>>();
    private final Map<E, AtomicInteger> flavorDispensedCountMap = new ConcurrentHashMap<E, AtomicInteger>();

    /**
     * Can only be instantiated from builder
     *
     * @param builder
     */
    private ConcurrentFlavorDispenser(FlavorDispenserBuilder<T, E> builder) {
        this.desiredInventory = builder.desiredInventory;
        this.flavorFactory = builder.flavorFactory;
        this.threadCount = builder.nThreads;
        this.timeoutMinutes = builder.timeoutMinutes;
        for (E flavor : flavorFactory.getAllFlavors()) {
            ConcurrentLinkedQueue<T> concurrentLinkedQueue = new ConcurrentLinkedQueue<T>();
            flavorInventory.put(flavor, concurrentLinkedQueue);
            flavorDispensedCountMap.put(flavor, new AtomicInteger(0));
        }
        backgroundRefillAll();
    }

    public int getInventoryCount(E flavor) {
        return flavorInventory.get(flavor)
                .size();
    }


    public Map<E, Integer> getDesiredInventory() {
        return desiredInventory;
    }

    private Map<E,Integer> getOrder() {
        Map<E, Integer> order = new ConcurrentHashMap<E, Integer>();
        for ( E flavor : flavorFactory.getAllFlavors()) {
            order.put(flavor, getFlavorOrderCount(flavor));
        }
        return order;
    }

    private Map<E,Integer> getFlavorOrder ( E flavor) {
        Map<E, Integer> order = new ConcurrentHashMap<E, Integer>();
        order.put(flavor, getFlavorOrderCount(flavor));
        return order;
    }

    private int getFlavorOrderCount(E flavor) {
        final int actualCount = flavorInventory.get(flavor)
                .size();
        final int expectedCount = desiredInventory.get(flavor);
        int orderSize = expectedCount - actualCount;

        if (orderSize < 0) {
            orderSize = 0;
        }
        return orderSize;
    }


    public T dispense(E flavor) {

        flavorDispensedCountMap.get(flavor)
                .incrementAndGet();
        logger.debug("dispenseCountMap=" + flavorDispensedCountMap);
        ConcurrentLinkedQueue<T> flavorQueue = flavorInventory.get(flavor);

        synchronized (flavor) {
            T instance = flavorQueue.poll();

            if (instance == null) {
                new RefillOperation<E,T>(this, flavor).run();;
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


    private void fulfillOrder(final Map<E, Integer> order) {
        addNewStock(new FlavorFactoryExecutor<T, E>(flavorFactory,
                order, threadCount, timeoutMinutes).execute());
    }

    private void addNewStock(Map<E, List<T>> flavorInstanceMap) {
        for (E flavor : flavorFactory.getAllFlavors()) {
            for (T instance : flavorInstanceMap.get(flavor)) {
                flavorInventory.get(flavor)
                        .add(instance);
            }
        }
    }

    public void backgroundRefillAll() {
        for ( E flavor : flavorFactory.getAllFlavors()) {
            Map<E, Integer> order = getFlavorOrder(flavor);
            addNewStock(new FlavorFactoryExecutor<T, E>(flavorFactory,
                    order, threadCount, timeoutMinutes).execute());
        }
    }

    public void backgroundRefill(E flavor) {
        Map<E, Integer> order = getFlavorOrder(flavor);
        addNewStock(new FlavorFactoryExecutor<T, E>(flavorFactory,
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

    private static class RefillOperation<E extends Enum<E>, T> implements Runnable {

        private ConcurrentFlavorDispenser dispenser;
        private E flavor;

        private RefillOperation(ConcurrentFlavorDispenser dispenser, E flavor) {
            this.dispenser = dispenser;
            this.flavor = flavor;
        }

        public void run() {
            dispenser.fulfillOrder(dispenser.getFlavorOrder(flavor));
        }
    }
}
