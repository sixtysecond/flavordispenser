package org.sixtysecs.util.dispenser;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by edriggs on 10/10/15.
 */
public abstract class AbstractDispenser<T, E extends Enum<E>> implements FlavorDispenser<T, E> {
    protected final Logger logger = Logger.getLogger(AbstractDispenser.class);


    protected final Map<E, Integer> desiredInventory;
    protected final FlavorFactory<T, E> flavorFactory;
    protected final Map<E, ConcurrentLinkedQueue<T>> flavorInventory = new ConcurrentHashMap<E, ConcurrentLinkedQueue<T>>();
    protected final Map<E, AtomicInteger> flavorDispensedCountMap = new ConcurrentHashMap<E, AtomicInteger>();

    protected AbstractDispenser(Map<E, Integer> desiredInventory, FlavorFactory<T, E> flavorFactory) {
        this.desiredInventory = desiredInventory;
        this.flavorFactory = flavorFactory;
    }

    protected void addStockToInventory(Map<E, List<T>> flavorInstanceMap) {
        for (E flavor : flavorFactory.getAllFlavors()) {
            for (T instance : flavorInstanceMap.get(flavor)) {
                flavorInventory.get(flavor)
                        .add(instance);
            }
        }
    }

    public Map<E, Integer> getDesiredInventory() {
        return desiredInventory;
    }

    public int getInventoryCount(E flavor) {
        return flavorInventory.get(flavor)
                .size();
    }

    protected Map<E, Integer> getOrder() {
        Map<E, Integer> order = new ConcurrentHashMap<E, Integer>();
        for (E flavor : flavorFactory.getAllFlavors()) {
            order.put(flavor, getFlavorOrderCount(flavor));
        }
        return order;
    }

    protected Map<E, Integer> getFlavorOrder(E flavor) {
        Map<E, Integer> order = new ConcurrentHashMap<E, Integer>();
        order.put(flavor, getFlavorOrderCount(flavor));
        return order;
    }

    protected int getFlavorOrderCount(E flavor) {
        final int actualCount = flavorInventory.get(flavor)
                .size();
        final int expectedCount = desiredInventory.get(flavor);
        int orderSize = expectedCount - actualCount;

        if (orderSize < 0) {
            orderSize = 0;
        }
        return orderSize;
    }

}
