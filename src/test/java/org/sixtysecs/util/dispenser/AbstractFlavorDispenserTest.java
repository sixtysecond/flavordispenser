package org.sixtysecs.util.dispenser;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class AbstractFlavorDispenserTest {

	@BeforeMethod
	public void nameBefore(Method method)
	{
		System.out.println("==== " +  getClass().getSimpleName() + "::" + method.getName() + " ====");
	}


	@Test
	public void dispenseTest() {
		FlavorDispenser<Beverage, BeverageFlavor> flavorDispenser = new ConcurrentFlavorDispenser.FlavorDispenserBuilder<Beverage, BeverageFlavor>(
				new BeverageFlavorFactory()).build();
		{
			Beverage beverage = flavorDispenser
					.dispense(BeverageFlavor.BOTTLED_WATER);
			assertNotNull(beverage);
			assertEquals(beverage.getBeverageFlavor(),
					BeverageFlavor.BOTTLED_WATER);
		}
		{
			Beverage beverage = flavorDispenser.dispense(BeverageFlavor.COLA);
			assertNotNull(beverage);
			assertEquals(beverage.getBeverageFlavor(), BeverageFlavor.COLA);
		}
		{
			Beverage beverage = flavorDispenser
					.dispense(BeverageFlavor.ROOT_BEER);
			assertNotNull(beverage);
			assertEquals(beverage.getBeverageFlavor(), BeverageFlavor.ROOT_BEER);
		}
	}

	@Test
	public void whenDesiredInventoryFive_HaveFiveAfterDispenseTest() throws InterruptedException {

		FlavorDispenser<Beverage, BeverageFlavor> flavorDispenser = new ConcurrentFlavorDispenser.FlavorDispenserBuilder<Beverage, BeverageFlavor>(
				new BeverageFlavorFactory()).setDesiredlFlavorInventory(BeverageFlavor.COLA,5).build();
		assertEquals(flavorDispenser.getInventoryCount(BeverageFlavor.COLA), 5);
		{
			Beverage beverage1 = flavorDispenser.dispense(BeverageFlavor.COLA);
			assertNotNull(beverage1);
			assertEquals(beverage1.getBeverageFlavor(), BeverageFlavor.COLA);
			assertEquals(flavorDispenser.getInventoryCount(BeverageFlavor.COLA), 5);
		}
		{
			Beverage beverage2 = flavorDispenser.dispense(BeverageFlavor.COLA);
			assertNotNull(beverage2);
			assertEquals(beverage2.getBeverageFlavor(), BeverageFlavor.COLA);
			assertEquals(flavorDispenser.getInventoryCount(BeverageFlavor.COLA), 5);
		}
		{
			Beverage beverage3 = flavorDispenser.dispense(BeverageFlavor.COLA);
			assertNotNull(beverage3);
			assertEquals(beverage3.getBeverageFlavor(), BeverageFlavor.COLA);
			assertEquals(flavorDispenser.getInventoryCount(BeverageFlavor.COLA), 5);
		}
	}
}
