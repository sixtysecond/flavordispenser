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
        return new SelfRefillingSelectionDispenser<Crayon, CrayonColor>( new CrayonFactory(), null);
    }

    @Test
    public void whenInstantiateSetsDefaultInitialInventoryTest() {
        final Integer expectedBlueCount = 1;
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        for (CrayonColor selection : dispenser.getSelections()) {
            Assert.assertEquals(dispenser.getInventoryCount().get(
                    selection), expectedBlueCount);
        }
    }

    @Test
    public void whenInstantiateSetsDesiredInventoryTest() {
        final Integer expectedBlueCount = 3;
        final Integer expectedGreenCount = 2;
        final Integer expectedRedCount = 1;

        Map<CrayonColor, Integer> desiredInventory = new HashMap<CrayonColor, Integer>();
        desiredInventory.put(CrayonColor.BLUE, expectedBlueCount);
        desiredInventory.put(CrayonColor.GREEN, expectedGreenCount);
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser =
                new SelfRefillingSelectionDispenser<Crayon, CrayonColor>(new CrayonFactory(), desiredInventory);

        Assert.assertEquals(dispenser.getInventoryCount().get(
                CrayonColor.BLUE), expectedBlueCount);

        Assert.assertEquals(dispenser.getInventoryCount().get(
                CrayonColor.GREEN), expectedGreenCount);

        Assert.assertEquals(dispenser.getInventoryCount().get(
                CrayonColor.RED), expectedRedCount);

    }

    @Test
    public void whenHasInventoryForAllSelectionAfterCreateTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        for (CrayonColor selection : dispenser.getSelections()) {
            Assert.assertEquals(dispenser.getInventoryCount().get(
                    selection), new Integer(1));
        }
    }

    @Test
    public void hasMoreItemsAfterRefillTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Map<CrayonColor, Collection<Crayon>> newInventory = new HashMap<CrayonColor, Collection<Crayon>>();
        newInventory.put(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
                dispenser.addInventory(newInventory);
        Assert.assertEquals(dispenser.getInventoryCount().get(
                CrayonColor.BLUE), new Integer(2));
    }

    @Test
    public void inventoryDecreasesAfterDispenseTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Map<CrayonColor, Collection<Crayon>> newInventory = new HashMap<CrayonColor, Collection<Crayon>>();
        newInventory.put(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
        dispenser.addInventory(newInventory);
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertNotNull(crayon);
        Assert.assertEquals(crayon.getCrayonColor(), CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getInventoryCount().get(
                CrayonColor.BLUE), new Integer(1));
    }

    @Test
    public void whenEmptyDispenseNewTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Assert.assertEquals(dispenser.getInventoryCount().get(
                CrayonColor.BLUE), new Integer(1));
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getInventoryCount().get(
                CrayonColor.BLUE), new Integer(1));
    }
}
