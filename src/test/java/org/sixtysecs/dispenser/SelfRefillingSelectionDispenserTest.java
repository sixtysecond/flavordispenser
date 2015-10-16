package org.sixtysecs.dispenser;

import org.testng.annotations.Test;
import org.sixtysecs.dispenser.crayon.*;
import org.testng.Assert;

import java.util.*;

/**
 * Created by edriggs on 10/11/15.
 */
public class SelfRefillingSelectionDispenserTest {


    public BlockingSelfRefillingSelectionDispenser<Crayon, CrayonColor> getCrayonDispenser() {
        return new BlockingSelfRefillingSelectionDispenser<Crayon, CrayonColor>(new CrayonFactory(), null);
    }

    @Test
    public void whenInstantiateSetsDefaultInitialInventoryTest() {
        BlockingSelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        for (CrayonColor selection : dispenser.getSelections()) {
            Assert.assertEquals(dispenser.getSelectionInventoryCount(selection), 1);
        }
    }

    @Test
    public void whenInstantiateSetsDesiredInventoryTest() throws InterruptedException {
        final int expectedBlueCount = 3;
        final int expectedGreenCount = 2;
        final int expectedRedCount = 0;

        Map<CrayonColor, Integer> desiredInventory = new HashMap<CrayonColor, Integer>();
        desiredInventory.put(CrayonColor.BLUE, expectedBlueCount);
        desiredInventory.put(CrayonColor.GREEN, expectedGreenCount);
        BlockingSelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser =
                new BlockingSelfRefillingSelectionDispenser<Crayon, CrayonColor>(new CrayonFactory(), desiredInventory);
        Thread.sleep(100);

        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), expectedBlueCount);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.GREEN), expectedGreenCount);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.RED), expectedRedCount);
    }

    @Test
    public void whenHasInventoryForAllSelectionAfterCreateTest() {
        BlockingSelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        for (CrayonColor selection : dispenser.getSelections()) {
            Assert.assertEquals(dispenser.getSelectionInventoryCount(selection), 1);
        }
    }

    @Test
    public void hasMoreItemsAfterRefillTest() {
        BlockingSelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Map<CrayonColor, Collection<Crayon>> newInventory = new HashMap<CrayonColor, Collection<Crayon>>();
        newInventory.put(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
        dispenser.addInventory(newInventory);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 1);
    }

    @Test
    public void inventoryDecreasesAfterDispenseTest() {
        BlockingSelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Map<CrayonColor, Collection<Crayon>> newInventory = new HashMap<CrayonColor, Collection<Crayon>>();
        newInventory.put(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
        dispenser.addInventory(newInventory);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 1);
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 0);
    }

    @Test
    public void whenEmptyDispenseNewTest() {
        BlockingSelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 0);
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 0);
    }
}
