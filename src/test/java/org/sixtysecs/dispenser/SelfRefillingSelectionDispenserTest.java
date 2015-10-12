package org.sixtysecs.dispenser;

import org.junit.Test;
import org.testng.Assert;

import java.util.Arrays;

/**
 * Created by edriggs on 10/11/15.
 */
public class SelfRefillingSelectionDispenserTest {

    Integer ZERO = new Integer(0);
    Integer ONE = new Integer(1);

    public SelfRefillingSelectionDispenser<Crayon, CrayonColor> getCrayonDispenser() {
        return new SelfRefillingSelectionDispenser<Crayon, CrayonColor>(CrayonColor.class, new CrayonFactory());
    }

    @Test
    public void emptyAfterCreateTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Assert.assertEquals(dispenser.getInventoryCount()
                .get(CrayonColor.BLUE), ZERO);
    }

    @Test
    public void hasItemsAfterRefillTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        dispenser.addInventory(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
        Assert.assertEquals(dispenser.getInventoryCount()
                .get(CrayonColor.BLUE), ONE);
    }

    @Test
    public void inventoryDecreasesAfterDispenseTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        dispenser.addInventory(CrayonColor.BLUE, Arrays.asList(new Crayon(CrayonColor.BLUE)));
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertNotNull(crayon);
        Assert.assertEquals(crayon.getCrayonColor(), CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getInventoryCount()
                .get(CrayonColor.BLUE), ZERO);
    }

    @Test
    public void whenEmptyDispenseNewTest() {
        SelfRefillingSelectionDispenser<Crayon, CrayonColor> dispenser = getCrayonDispenser();
        Assert.assertEquals(dispenser.getInventoryCount()
                .get(CrayonColor.BLUE), ZERO);
        Crayon crayon = dispenser.dispense(CrayonColor.BLUE);
        Assert.assertEquals(dispenser.getInventoryCount()
                .get(CrayonColor.BLUE), ZERO);
    }
}
