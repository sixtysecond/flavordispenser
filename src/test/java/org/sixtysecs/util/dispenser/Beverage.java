package org.sixtysecs.util.dispenser;


/**
 * An example return type T for FlavorDispenser testing. This class illustrates
 * how properties from the flavor enumerator or the enumerator itself may be
 * used when instantiating the return type.
 * 
 * @author edriggs
 * 
 */
public class Beverage {
	private final BeverageFlavor beverageFlavor;
	private final int calories;

	public Beverage(BeverageFlavor beverageFlavor) {
		this.beverageFlavor = beverageFlavor;
		this.calories = beverageFlavor.getCalories();
	}

	public BeverageFlavor getBeverageFlavor() {
		return beverageFlavor;
	}

	public int getCalories() {
		return calories;
	}
}
