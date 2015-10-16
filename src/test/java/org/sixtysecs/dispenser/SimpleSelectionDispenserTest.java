package org.sixtysecs.dispenser;

import org.junit.Test;
import org.sixtysecs.dispenser.crayon.Crayon;
import org.sixtysecs.dispenser.crayon.CrayonColor;
import org.testng.Assert;

import java.util.*;

/**
 * Created by edriggs on 10/11/15.
 */
public class SimpleSelectionDispenserTest {

    Integer ZERO = new Integer(0);
    Integer ONE = new Integer(1);

    public ConcurrentSelectionDispenser<Crayon, CrayonColor> getCrayonDispenser() {
        return new ConcurrentSelectionDispenser<Crayon, CrayonColor>();
    }

    @Test
    public void emptyAfterCreateTest() {
        ConcurrentSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Assert.assertEquals(dispenser.getInventoryCount()
                .get(CrayonColor.BLUE), ZERO);
    }

    @Test
    public void hasItemsAfterRefillTest() {
        ConcurrentSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Map<CrayonColor, Collection<Crayon>> newInventory = new HashMap<CrayonColor, Collection<Crayon>>();
        newInventory.put(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
        dispenser.addInventory(newInventory);
        Assert.assertEquals(dispenser.getInventoryCount()
                .get(CrayonColor.BLUE), ONE);
    }

    @Test
    public void inventoryDecreasesAfterDispenseTest() {
        ConcurrentSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Map<CrayonColor, Collection<Crayon>> newInventory = new HashMap<CrayonColor, Collection<Crayon>>();
        newInventory.put(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
        dispenser.addInventory(newInventory);
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertNotNull(crayon);
        Assert.assertEquals(crayon.getCrayonColor(), CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getInventoryCount()
                .get(CrayonColor.BLUE), ZERO);
    }
}
