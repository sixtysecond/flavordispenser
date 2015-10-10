package org.sixtysecs.util.dispenser;

import java.util.EnumSet;


/**
 * An example FlavorFactory for testing.
 * 
 * @author edriggs
 *
 */
public class BeverageFlavorFactory implements
		FlavorFactory<Beverage, BeverageFlavor> {

	public Beverage newInstance(BeverageFlavor flavor) {
		return new Beverage(flavor);
	}

	public EnumSet<BeverageFlavor> getAllFlavors() {
		return EnumSet.allOf(BeverageFlavor.class);
	}
}
