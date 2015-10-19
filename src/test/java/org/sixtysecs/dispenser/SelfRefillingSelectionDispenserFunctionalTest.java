package org.sixtysecs.dispenser;

import org.sixtysecs.dispenser.crayon.*;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by edriggs on 10/11/15.
 */
public class SelfRefillingSelectionDispenserFunctionalTest {

    @BeforeMethod
    public void nameBefore(Method method)
    {
        System.out.println("==== " +  getClass().getSimpleName() + "::" + method.getName() + " ====");
    }

    public SelfRefillingSelectionDispenser<Crayon, CrayonColor> getCrayonDispenser() {
        return new SelfRefillingSelectionDispenser<Crayon, CrayonColor>(new CrayonFactory());
    }

    @Test
    public void whenInstantiateSetsDefaultInitialInventoryTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
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
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser =
                new SelfRefillingSelectionDispenser<Crayon, CrayonColor>(new CrayonFactory());
        dispenser.setDesiredInventory(desiredInventory);
        Thread.sleep(100);

        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), expectedBlueCount);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.GREEN), expectedGreenCount);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.RED), expectedRedCount);
    }

    @Test
    public void whenHasInventoryForAllSelectionAfterCreateTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        for (CrayonColor selection : dispenser.getSelections()) {
            Assert.assertEquals(dispenser.getSelectionInventoryCount(selection), 1);
        }
    }

    @Test
    public void hasMoreItemsAfterRefillTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        dispenser.addInventory(CrayonColor.BLUE, new Crayon(CrayonColor.BLUE));
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 1);
    }

    @Test
    public void inventoryDecreasesAfterDispenseTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        dispenser.addInventory(CrayonColor.BLUE, new Crayon(CrayonColor.BLUE));
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 1);
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 0);
    }

    @Test
    public void whenEmptyDispenseNewTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 0);
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 0);
    }

    @Test
    public void createInventoryTest() throws InterruptedException {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 0);
        dispenser.createInventory(CrayonColor.BLUE, 3);
        Thread.sleep(20);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 3);
    }
}
