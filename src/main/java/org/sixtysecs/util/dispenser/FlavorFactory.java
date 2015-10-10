package org.sixtysecs.util.dispenser;

import java.util.EnumSet;

/**
 * A FlavorFactory instantiates objecs of type T using an enum E to define the instatiation parameters.
 * <p>
 * Each factory and its enumerator may have its own distinct set of properties
 * appropriate to the domain of instantiating that type of object.
 * 
 * @author edriggs
 * 
 * @param <T>
 *            the type instantiated
 * @param <E>
 *            the flavor specifying the instantiation options
 */
public interface FlavorFactory<T, E extends Enum<E>> {

	/**
	 * May return null. It is up to the factory's discretion whether to retry if
	 * the initial attempt to instantiate fails.
	 * 
	 * @param flavor
	 *            the flavor which determines the instantiation properties
	 * @return a new instance of the associated type or <code>NULL</code>
	 */
	public T newInstance(E flavor);

	/**
	 * Should be implemented as
	 *
	 * <pre>
	 * return EnumSet.AllOf(YourFlavorEnum.class);
	 * </pre>
	 *
	 * Helper method needed for generic code to iterate through enumerator.
	 *
	 * @return an EnumSet containing all flavors which the factory can produce.
	 *         (A factory should be able to produce an instance for each flavor
	 *         defined in its enumerator).
	 */
	public EnumSet<E> getAllFlavors();
}
