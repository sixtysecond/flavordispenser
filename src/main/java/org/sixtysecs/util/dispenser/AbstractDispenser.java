//package org.sixtysecs.util.dispenser;
//
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * Created by edriggs on 10/10/15.
// */
//public abstract class AbstractDispenser<T,E> implements FlavorDispenser {
//
//    private final Map<E, Integer> desiredInventory;
//    private final FlavorFactory<T, E> flavorFactory;
//
//    /**
//     * Can only be instantiated from builder
//     *
//     * @param builder
//     */
//    private AbstractDispenser(FlavorDispenserBuilder<T, E> builder) {
//        this.desiredInventory = builder.desiredInventory;
//        this.flavorFactory = builder.flavorFactory;
//        this.threadCount = builder.nThreads;
//        this.timeoutMinutes = builder.timeoutMinutes;
//        for (E flavor : flavorFactory.getAllFlavors()) {
//            ConcurrentLinkedQueue<T> concurrentLinkedQueue = new ConcurrentLinkedQueue<T>();
//            flavorInventory.put(flavor, concurrentLinkedQueue);
//            flavorDispensedCountMap.put(flavor, new AtomicInteger(0));
//        }
//        backgroundRefillAll();
//    }
//
//    private final Map<E, ConcurrentLinkedQueue<T>> flavorInventory = new ConcurrentHashMap<E, ConcurrentLinkedQueue<T>>();
//    private final Map<E, AtomicInteger> flavorDispensedCountMap = new ConcurrentHashMap<E, AtomicInteger>();
//
//    public int getInventoryCount(E flavor) {
//        return flavorInventory.get(flavor)
//                .size();
//    }
//
//    public Map getDesiredInventory() {
//        return null;
//    }
//}
