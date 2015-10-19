package org.sixtysecs.dispenser;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.sixtysecs.dispenser.crayon.Crayon;
import org.sixtysecs.dispenser.crayon.CrayonColor;
import org.testng.Assert;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by edriggs on 10/11/15.
 */
public class SimpleSelectionDispenserTest {

    @BeforeMethod
    public void nameBefore(Method method)
    {
        System.out.println("==== " +  getClass().getSimpleName() + "::" + method.getName() + " ====");
    }

    public SimpleSelectionDispenser<Crayon, CrayonColor> getCrayonDispenser() {
        return new SimpleSelectionDispenser<Crayon, CrayonColor>();
    }

    @Test
    public void selectionInventoryCountZeroAfterCreateTest() {
        SimpleSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        for ( CrayonColor crayonColor : EnumSet.allOf(CrayonColor.class)) {
            Assert.assertEquals(dispenser.getSelectionInventoryCount(crayonColor), 0);
        }
    }

    @Test
    public void hasItemsAfterRefillTest() {
        SimpleSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        dispenser.addInventory(CrayonColor.BLUE, new Crayon(CrayonColor.BLUE));
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 1);
    }

    @Test
    public void inventoryDecreasesAfterDispenseTest() {
        SimpleSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        dispenser.addInventory(CrayonColor.BLUE, new Crayon(CrayonColor.BLUE));
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertNotNull(crayon);
        Assert.assertEquals(crayon.getCrayonColor(), CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getSelectionInventoryCount(CrayonColor.BLUE), 0);
    }
}
