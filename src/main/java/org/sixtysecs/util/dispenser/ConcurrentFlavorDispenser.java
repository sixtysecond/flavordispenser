package org.sixtysecs.util.dispenser;

import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A thread-safe implementation of {@link FlavorDispenser} Can only be
 * instantiated through {@link ConcurrentFlavorDispenser.FlavorDispenserBuilder}.
 * <p>
 * Only the following properties have mutable state:
 * <ul>
 * <li>flavorQueueMap</li>
 * <li>flavorDispensedCountMap</li>
 * </ul>
 * {@see FlavorDispenser}
 * 
 * @param <T>
 *            the type instantiated
 * @param <E>
 *            the flavor specifying the instantiation options
 */
public class ConcurrentFlavorDispenser<T, E extends Enum<E>> implements
		FlavorDispenser<T, E> {

	protected final Logger logger = Logger.getLogger(ConcurrentFlavorDispenser.class);

	private final int threadCount;
	private final int timeoutMinutes;

	private final Map<E, Integer> initialInventoryCount;
	private final int refillAmount;
	private final boolean isBackgroundRefill;
	private final int backgroundRefillThreshold;
	private final int backgroundRefillAmount;
	private final FlavorFactory<T, E> flavorFactory;

	private final Map<E, ConcurrentLinkedQueue<T>> flavorQueueMap = new ConcurrentHashMap<E, ConcurrentLinkedQueue<T>>();
	private final Map<E, AtomicInteger> flavorDispensedCountMap = new ConcurrentHashMap<E, AtomicInteger>();

	/**
	 * Can only be instantiated from builder
	 * 
	 * @param builder
	 */
	private ConcurrentFlavorDispenser(FlavorDispenserBuilder<T, E> builder) {
		this.initialInventoryCount = builder.initialInventoryCount;

		this.refillAmount = builder.refillAmount;
		this.isBackgroundRefill = builder.isBackgroundRefill;
		this.backgroundRefillThreshold = builder.backgroundRefillThreshold;
		this.backgroundRefillAmount = builder.backgroundRefillAmount;
		this.flavorFactory = builder.flavorFactory;
		this.threadCount = builder.nThreads;
		this.timeoutMinutes = builder.timeoutMinutes;
		for (E flavor : flavorFactory.getAllFlavors()) {
			ConcurrentLinkedQueue<T> concurrentLinkedQueue = new ConcurrentLinkedQueue<T>();
			flavorQueueMap.put(flavor, concurrentLinkedQueue);
			flavorDispensedCountMap.put(flavor, new AtomicInteger(0));
		}
	}


	public int getInventoryCount(E flavor) {
		return flavorQueueMap.get(flavor).size();
	}


	public int getRefillAmount() {
		return refillAmount;
	}


	public boolean isBackgroundRefill() {
		return isBackgroundRefill;
	}


	public int getBackgroundRefillThreshold() {
		return backgroundRefillThreshold;
	}


	public int getBackgroundRefillAmount() {
		return backgroundRefillAmount;
	}


	public Map<E, Integer> getInitialInventoryCount() {
		return initialInventoryCount;
	}

	public T dispense(E flavor) {
		flavorDispensedCountMap.get(flavor).incrementAndGet();
		logger.debug("dispenseCountMap=" + flavorDispensedCountMap);
		
		ConcurrentLinkedQueue<T> enumQueue = flavorQueueMap.get(flavor);
		backgroundRefillFlavorIfLow(flavor);
		
		synchronized (flavor) {
			T instance = enumQueue.poll();
			
			if (instance == null) {
				refillFlavor(flavor);
				instance = enumQueue.poll();
				if (instance == null) {
					throw new NullPointerException("Unable to dispense: "
							+ flavor.name());
				}
			}
			return instance;
		}
	}

	private void refillFlavor(E flavor) {
		Map<E, Integer> flavorCountMap = new HashMap<E, Integer>();
		flavorCountMap.put(flavor, refillAmount);
		refillFlavors(flavorCountMap);
	}

	private void refillFlavors(Map<E, Integer> flavorCountMap) {
		addToQueues(new FlavorFactoryExecutor<T, E>(flavorFactory,
				flavorCountMap, threadCount, timeoutMinutes).execute());
	}

	private void addToQueues(Map<E, List<T>> flavorInstanceMap) {
		for (E flavor : flavorFactory.getAllFlavors()) {
			for (T instance : flavorInstanceMap.get(flavor)) {
				flavorQueueMap.get(flavor).add(instance);
			}
		}
	}

	private void backgroundRefillFlavorIfLow(final E flavor) {
		System.out.println("backgroundRefillFlavorIfLow");
		if (flavorQueueMap.get(flavor).size() <= backgroundRefillThreshold) {
			new Thread() {
				public void run() {
					Map<E, Integer> flavorRefillMap = new HashMap<E, Integer>();
					flavorRefillMap.put(flavor, backgroundRefillAmount);
					refillFlavors(flavorRefillMap);
				}
			}.run();
		}
	}

	/**
	 * Refills a particular queue by the specified count. init() must have
	 * already been called before calling this.
	 * 
	 * @param flavor
	 * @param count
	 * @throws InterruptedException
	 */
	protected final void refillQueue(E flavor, int count)
			throws InterruptedException {
		Map<E, Integer> flavorCountMap = new HashMap<E, Integer>();
		flavorCountMap.put(flavor, count);
		refillQueues(flavorCountMap);
	}

	/**
	 * Refills the queues and sizes specified in the map init() must have
	 * already been called before calling this.
	 * 
	 * @param flavorCountMap
	 * @throws InterruptedException
	 */
	protected final synchronized void refillQueues(
			Map<E, Integer> flavorCountMap) throws InterruptedException {

		// TODO add to builder
		int nThreads = 100;
		int timeoutMinutes = 20;

		{

			Map<E, List<T>> executorQueueMap = new FlavorFactoryExecutor<T, E>(
					flavorFactory, flavorCountMap, nThreads, timeoutMinutes)
					.execute();

			for (E flavor : executorQueueMap.keySet()) {
				List<T> instanceList = executorQueueMap.get(flavor);
				for (T instance : instanceList) {
					flavorQueueMap.get(flavor).add(instance);
				}
			}
		}
	}

	/**
	 * A mutable builder for instantiating FlavorDispensers
	 * <p>
	 * flavorFactory is a required constructor parameter and cannot be
	 * <code>NULL</code>.
	 * 
	 * All other parameters are optional, are accessible through a fluent
	 * interface of setters, and have the following default behavior:
	 * <ul>
	 * <li>Collections default to empty</li>
	 * <li>booleans default to true</li>
	 * <li>Integers cannot be set below 1. Default values are specified on
	 * setter contracts.</li>
	 * </ul>
	 * 
	 * @author edriggs
	 * 
	 * @param <T>
	 *            the type instantiated
	 * @param <E>
	 *            the flavor specifying the instantiation options
	 */
	public static class FlavorDispenserBuilder<T, E extends Enum<E>> {

		private FlavorFactory<T, E> flavorFactory;
		private Map<E, Integer> initialInventoryCount;
		private Integer refillAmount;
		private Boolean isBackgroundRefill;
		private Integer backgroundRefillAmount;
		private Integer backgroundRefillThreshold;
		private Integer nThreads;
		private Integer timeoutMinutes;

		/**
		 * 
		 * @param flavorFactory
		 *            . The flavor factory to use. Cannot be <code>NULL<code>.
		 */
		public FlavorDispenserBuilder(FlavorFactory<T, E> flavorFactory) {
			if (flavorFactory == null) {
				throw new NullPointerException("flavorFactory");
			}
			this.flavorFactory = flavorFactory;
		}

		/**
		 * Optional, defaults to empty initial inventory.
		 * 
		 * @param initialInventoryCount
		 */
		public FlavorDispenserBuilder<T, E> setInitialInventory(
				Map<E, Integer> initialInventoryCount) {
			this.initialInventoryCount = initialInventoryCount;
			return this;
		}

		/**
		 * Optional. Defaults to 20. Values below 1 are ignored.
		 * 
		 * @param threadCount
		 *            the number of threads to use when refilling
		 * @return
		 */
		public FlavorDispenserBuilder<T, E> setThreadCount(Integer threadCount) {
			this.nThreads = threadCount;
			return this;
		}

		/**
		 * Optional. Defaults to 60. Values below 1 are ignored.
		 * 
		 * @param timeoutMinutes
		 * @return
		 */
		public FlavorDispenserBuilder<T, E> setTimeoutMinutes(
				Integer timeoutMinutes) {
			this.timeoutMinutes = timeoutMinutes;
			return this;
		}

		/**
		 * Optional. Defaults to 1. Values below 1 are ignored.
		 * 
		 * @param refillAmount
		 */
		public FlavorDispenserBuilder<T, E> setRefillAmount(int refillAmount) {
			this.refillAmount = refillAmount;
			return this;
		}

		/**
		 * Optional. Defaults to true.
		 * 
		 * @param isBackgroundRefill
		 */
		public FlavorDispenserBuilder<T, E> setIsBackgroundRefill(
				boolean isBackgroundRefill) {
			this.isBackgroundRefill = isBackgroundRefill;
			return this;
		}

		/**
		 * Optional. Defaults to 1. Values below 1 are ignored.
		 * 
		 * @param backgroundRefillThreshold
		 */
		public FlavorDispenserBuilder<T, E> setBackGroundRefillThreshold(
				int backgroundRefillThreshold) {
			this.backgroundRefillThreshold = backgroundRefillThreshold;
			return this;
		}

		/**
		 * Optional. Defaults to 1. Values below 1 are ignored.
		 * 
		 * @param backgroundRefillAmount
		 */
		public FlavorDispenserBuilder<T, E> setBackgroundRefillAmount(
				int backgroundRefillAmount) {
			this.backgroundRefillAmount = backgroundRefillAmount;
			return this;
		}

		/**
		 * 
		 * @return a FlavorDispenser
		 */
		public FlavorDispenser<T, E> build() {
			if (flavorFactory == null) {
				throw new NullPointerException("flavorFactory");
			}
			if (initialInventoryCount == null) {
				initialInventoryCount = new HashMap<E, Integer>();
			}
			if (refillAmount == null || refillAmount < 1) {
				refillAmount = 1;
			}
			if (isBackgroundRefill == null) {
				isBackgroundRefill = true;
			}
			if (backgroundRefillThreshold == null
					|| backgroundRefillThreshold < 1) {
				backgroundRefillThreshold = 1;
			}
			if (backgroundRefillAmount == null || backgroundRefillAmount < 1) {
				backgroundRefillAmount = 1;
			}
			if (nThreads == null || nThreads < 1) {
				nThreads = 20;
			}
			if (timeoutMinutes == null || timeoutMinutes < 1) {
				timeoutMinutes = 60;
			}
			return new ConcurrentFlavorDispenser<T, E>(this);
		}

	}

}
