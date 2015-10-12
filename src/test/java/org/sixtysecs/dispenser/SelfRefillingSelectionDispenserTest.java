package org.sixtysecs.dispenser;

import org.junit.Test;
import org.sixtysecs.dispenser.crayon.*;
import org.testng.Assert;

import java.util.*;

/**
 * Created by edriggs on 10/11/15.
 */
public class SelfRefillingSelectionDispenserTest {


    public SelfRefillingSelectionDispenser<Crayon, CrayonColor> getCrayonDispenser() {
        return new SelfRefillingSelectionDispenser<Crayon, CrayonColor>(CrayonColor.class, new CrayonFactory(), null);
    }

    @Test
    public void whenInstantiateSetsDefaultInitialInventoryTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        for (CrayonColor selection : dispenser.getSelections()) {
            Assert.assertEquals(dispenser.getSelectionInventoryCount(
                    selection), 1);
        }
    }

    @Test
    public void whenInstantiateSetsDesiredInventoryTest() {
        final int expectedBlueCount = 3;
        final int expectedGreenCount = 2;
        final int expectedRedCount = 1;

        Map<CrayonColor, Integer> desiredInventory = new HashMap<CrayonColor, Integer>();
        desiredInventory.put(CrayonColor.BLUE, expectedBlueCount);
        desiredInventory.put(CrayonColor.GREEN, expectedGreenCount);
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser =
                new SelfRefillingSelectionDispenser<Crayon, CrayonColor>(CrayonColor.class, new CrayonFactory(), desiredInventory);

        Assert.assertEquals(dispenser.getSelectionInventoryCount(
                CrayonColor.BLUE), expectedBlueCount);

        Assert.assertEquals(dispenser.getSelectionInventoryCount(
                CrayonColor.GREEN), expectedGreenCount);

        Assert.assertEquals(dispenser.getSelectionInventoryCount(
                CrayonColor.RED), expectedRedCount);

    }

    @Test
    public void whenHasInventoryForAllSelectionAfterCreateTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        for (CrayonColor selection : dispenser.getSelections()) {
            Assert.assertEquals(dispenser.getSelectionInventoryCount(
                    selection), 1);
        }
    }

    @Test
    public void hasMoreItemsAfterRefillTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        dispenser.addInventory(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
        Assert.assertEquals(dispenser.getSelectionInventoryCount(
                CrayonColor.BLUE), 2);
    }

    @Test
    public void inventoryDecreasesAfterDispenseTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        dispenser.addInventory(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertNotNull(crayon);
        Assert.assertEquals(crayon.getCrayonColor(), CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(
                CrayonColor.BLUE), 1);
    }

    @Test
    public void whenEmptyDispenseNewTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Assert.assertEquals(dispenser.getSelectionInventoryCount(
                CrayonColor.BLUE), 1);
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(
                CrayonColor.BLUE), 1);
    }
}
