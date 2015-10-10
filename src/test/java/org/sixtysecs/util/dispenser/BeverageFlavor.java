package org.sixtysecs.util.dispenser;

/**
 * An example flavor for FlavorDispenser testing
 * @author edriggs
 *
 */
public enum BeverageFlavor {
	BOTTLED_WATER(0)//
	, COLA(300) //
	, ROOT_BEER(270);

	private BeverageFlavor(int calories) {
		this.calories = calories;
	}

	private final int calories;

	public int getCalories() {
		return calories;
	}
}
