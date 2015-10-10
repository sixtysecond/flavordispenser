package org.sixtysecs.util.dispenser;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;


/**
 * An executor service for concurrent execution using a
 * {@link FlavorFactory#newInstance(E flavor)}
 * 
 * @author edriggs
 * 
 * @param <T>
 *            the return type for the object instantiated
 * @param <E>
 *            the flavor to instantiate.
 */
class FlavorFactoryExecutor<T, E extends Enum<E>> {

	private static Logger logger = Logger
			.getLogger(FlavorFactoryExecutor.class);

	private final FlavorFactory<T, E> flavorFactory;
	private final Map<E, Integer> flavorCountMap;
	private final int threadCount;
	private final int timeoutMinutes;

	/**
	 * Returns an instance of FlavorFactoryExecutor. Note: No work is performed
	 * unless {@link FlavorFactoryExecutor#execute()} is called.
	 * 
	 * @param flavorFactory
	 *            The factory which will perform the instantiation
	 * @param flavorCountMap
	 *            how many of each flavor to instantiate.
	 * @param threadCount
	 *            how many threads to use
	 * @param timeoutMinutes
	 *            how long to wait for the executor to finish.
	 *            <p>
	 *            <b>WARNING: setting timeoutMinutes too low may prevent the
	 *            executor from being able to return any objects.</b>
	 */
	FlavorFactoryExecutor(FlavorFactory<T, E> flavorFactory,
			Map<E, Integer> flavorCountMap, int threadCount, int timeoutMinutes) {
		this.flavorFactory = flavorFactory;
		this.flavorCountMap = flavorCountMap;
		this.threadCount = threadCount;
		this.timeoutMinutes = timeoutMinutes;
	}

	/**
	 * Attempts to create an instance for each flavor specified in flavor list.
	 * No guarantee all requested flavors or flavor counts will be instantiated.
	 * 
	 * @return a map containing flavors and a list of each instance for that
	 *         flavor. Each list will never be <code>NULL</code>, but may be
	 *         empty, or may contain fewer instances than requested.
	 */
	 Map<E, List<T>> execute() {

		ExecutorService executor = Executors.newFixedThreadPool(threadCount);

		/* Submit callables and get map of futures */
		Map<E, List<Future<T>>> flavorFutureInstanceListMap = new HashMap<E, List<Future<T>>>();
		{
			for (E flavor : flavorFactory.getAllFlavors()) {
				flavorFutureInstanceListMap.put(flavor,
						new ArrayList<Future<T>>());
			}

			for (E flavor : flavorCountMap.keySet()) {
				for (int i = 0; i < flavorCountMap.get(flavor); i++) {
					FlavorFactoryCallable<T, E> callable = new FlavorFactoryCallable<T, E>(
							flavorFactory, flavor);
					Future<T> futureInstance = executor.submit(callable);
					flavorFutureInstanceListMap.get(flavor).add(futureInstance);

				}
			}
		}

		// Try to wait until finished. If interrupted, proceed.
		{
			executor.shutdown();
			try {
				executor.awaitTermination(timeoutMinutes, TimeUnit.MINUTES);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!executor.isTerminated()) {
				logger.warn("FlavorFactoryExecutor was interrupted and has not terminated yet.");
			}
		}

		/*
		 * Store each instance into its proper list based on flavor
		 */
		Map<E, List<T>> flavorInstanceListMap = new HashMap<E, List<T>>();
		{

			for (E flavor : flavorFactory.getAllFlavors()) {
				flavorInstanceListMap.put(flavor, new ArrayList<T>());
			}

			for (E flavor : flavorFactory.getAllFlavors()) {
				List<Future<T>> flavorFutureLIst = flavorFutureInstanceListMap
						.get(flavor);

				for (Future<T> future : flavorFutureLIst) {
					try {
						T instance = future.get();
						if (instance != null) {
							flavorInstanceListMap.get(flavor).add(instance);
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}

		{ // log if instance count is different than expected
			int expectedSize = 0;
			for (E flavor : flavorFactory.getAllFlavors()) {
				expectedSize += flavorFutureInstanceListMap.get(flavor).size();
			}

			int actualSize = 0;
			for (E flavor : flavorFactory.getAllFlavors()) {
				expectedSize += flavorInstanceListMap.get(flavor).size();
			}

			if (expectedSize != actualSize) {
				logger.warn("Expected flavorFutureInstanceListMap size:"
						+ expectedSize + ", Actual: " + actualSize);
			}
		}
		return flavorInstanceListMap;
	}
}
