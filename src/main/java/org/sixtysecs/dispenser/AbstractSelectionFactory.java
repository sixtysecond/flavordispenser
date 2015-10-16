package org.sixtysecs.dispenser;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by edriggs on 10/16/15.
 */
public abstract class AbstractSelectionFactory<T, E> implements SelectionFactory<T, E> {

    public Map<E, Collection<T>> fulfill(Map<E, Integer> order) {

        Map<E, Collection<T>> newInventory = new ConcurrentHashMap<E, Collection<T>>();
        for (Map.Entry<E, Integer> entry : order.entrySet()) {

            Integer count = entry.getValue();
            if (count != null) {
                E selection = entry.getKey();
                List<T> TList = new ArrayList<T>();
                for (int i = 0; i < count; i++) {
                    TList.add(createItem(selection));
                }
                newInventory.put(selection, TList);
            }
        }
        return newInventory;
    }

    public abstract T createItem(E selection);

}
