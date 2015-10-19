package org.sixtysecs.dispenser.crayon;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Created by edriggs on 10/18/15.
 */
public class AbstractSelectionFactoryTest{
    @Test

    public void fullFillOrderNumersTest() {
        CrayonFactory crayonFactory = new CrayonFactory();
        Map<CrayonColor, Integer> order = new HashMap<CrayonColor, Integer>();
        order.put(CrayonColor.BLUE, 2);
        order.put(CrayonColor.GREEN, 3);
        order.put(CrayonColor.RED, 4);
        Map<CrayonColor, Collection<Crayon>> newInventory = crayonFactory.fulfill(order);
        Assert.assertEquals(newInventory.get(CrayonColor.BLUE).size(), 2);
        Assert.assertEquals(newInventory.get(CrayonColor.GREEN).size(), 3);
        Assert.assertEquals(newInventory.get(CrayonColor.RED).size(), 4);
    }

    public void fullFillOrderNullTest() {
        CrayonFactory crayonFactory = new CrayonFactory();
        Map<CrayonColor, Integer> order = new HashMap<CrayonColor, Integer>();
        order.put(CrayonColor.BLUE, null);
        order.put(CrayonColor.GREEN, null);

        Map<CrayonColor, Collection<Crayon>> newInventory = crayonFactory.fulfill(order);
        Assert.assertEquals(newInventory.get(CrayonColor.BLUE).size(), 0);
        Assert.assertEquals(newInventory.get(CrayonColor.GREEN).size(), 0);
        Assert.assertEquals(newInventory.get(CrayonColor.RED).size(), 0);
    }
}
