package org.sixtysecs.dispenser;

import org.testng.annotations.Test;
import org.sixtysecs.dispenser.crayon.Crayon;
import org.sixtysecs.dispenser.crayon.CrayonColor;
import org.testng.Assert;

import java.util.*;

/**
 * Created by edriggs on 10/11/15.
 */
public class SimpleSelectionDispenserTest {

    public BlockingSelectionDispenser<Crayon, CrayonColor> getCrayonDispenser() {
        return new BlockingSelectionDispenser<Crayon, CrayonColor>();
    }

    @Test
    public void selectionInventoryCountZeroAfterCreateTest() {
        BlockingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        for ( CrayonColor crayonColor : EnumSet.allOf(CrayonColor.class)) {
            Assert.assertEquals(dispenser.getSelectionInventoryCount(crayonColor), 0);
        }
    }

    @Test
    public void hasItemsAfterRefillTest() {
        BlockingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Map<CrayonColor, Collection<Crayon>> newInventory = new HashMap<CrayonColor, Collection<Crayon>>();
        newInventory.put(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
        dispenser.addInventory(newInventory);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 1);
    }

    @Test
    public void inventoryDecreasesAfterDispenseTest() {
        BlockingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Map<CrayonColor, Collection<Crayon>> newInventory = new HashMap<CrayonColor, Collection<Crayon>>();
        newInventory.put(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
        dispenser.addInventory(newInventory);
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertNotNull(crayon);
        Assert.assertEquals(crayon.getCrayonColor(), CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 0);
    }
}
