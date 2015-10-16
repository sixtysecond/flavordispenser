package org.sixtysecs.dispenser.crayon;

import org.sixtysecs.dispenser.AbstractSelectionFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Example factory for refilling selection dispenser
 */
public class CrayonFactory extends AbstractSelectionFactory<Crayon, CrayonColor> {

    public Map<CrayonColor, Collection<Crayon>> fulfill(Map<CrayonColor, Integer> order) {

        Map<CrayonColor, Collection<Crayon>> newInventory = new ConcurrentHashMap<CrayonColor, Collection<Crayon>>();
        for (Map.Entry<CrayonColor, Integer> entry : order.entrySet()) {

            Integer count = entry.getValue();
            if (count != null) {
                CrayonColor selection = entry.getKey();
                List<Crayon> crayonList = new ArrayList<Crayon>();
                for (int i = 0; i < count; i++) {
                    crayonList.add(createItem(selection));
                }
                newInventory.put(selection, crayonList);
            }
        }
        return newInventory;
    }

    @Override
    public Crayon createItem(CrayonColor selection) {
        return new Crayon(selection);
    }
}
