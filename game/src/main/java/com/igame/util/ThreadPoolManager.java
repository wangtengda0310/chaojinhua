package com.igame.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 
 * @author zhh
 *
 */

public final class ThreadPoolManager {

	private static final long MAX_DELAY = TimeUnit.NANOSECONDS.toMillis(Long.MAX_VALUE - System.nanoTime()) / 2;

	public static final long MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING = ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING;

	private final ScheduledThreadPoolExecutor scheduledPool;
	private final ThreadPoolExecutor instantPool;


	/**
	 * @return ThreadPoolManager instance.
	 */
	private static final class SingletonHolder {
		private static final ThreadPoolManager INSTANCE = new ThreadPoolManager();
	}

	public static ThreadPoolManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	/**
	 * Constructor.
	 */
	private ThreadPoolManager() {
		final int instantPoolSize = Math.max(1, ThreadConfig.THREAD_POOL_SIZE/3);

		scheduledPool = new ScheduledThreadPoolExecutor(ThreadConfig.THREAD_POOL_SIZE - instantPoolSize);
		scheduledPool.setRejectedExecutionHandler(new AnRejectedExecutionHandler());
		scheduledPool.prestartAllCoreThreads();

		instantPool = new ThreadPoolExecutor(instantPoolSize, instantPoolSize * 2, 5L, TimeUnit.SECONDS,
				new ArrayBlockingQueue<Runnable>(10000));
		instantPool.setRejectedExecutionHandler(new AnRejectedExecutionHandler());
		instantPool.prestartAllCoreThreads();

		scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				purge();
			}
		}, 60000, 60000);


	}

	private final long validate(long delay) {
		return Math.max(0, Math.min(MAX_DELAY, delay));
	}

	private static final class ThreadPoolExecuteWrapper extends ExecuteWrapper {
		private ThreadPoolExecuteWrapper(Runnable runnable) {
			super(runnable);
		}

		@Override
		protected long getMaximumRuntimeInMillisecWithoutWarning() {
			return ThreadConfig.MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING;
		}
	}

	// ===========================================================================================

	public final ScheduledFuture<?> schedule(Runnable r, long delay) {
		r = new ThreadPoolExecuteWrapper(r);
//		delay = validate(delay);
		return new ScheduledFutureWrapper(scheduledPool.schedule(r, delay, TimeUnit.MILLISECONDS));
	}


	// ===========================================================================================

	public final ScheduledFuture<?> scheduleAtFixedRate(Runnable r, long delay, long period) {
		r = new ThreadPoolExecuteWrapper(r);
//		delay = validate(delay);
//		period = validate(period);

		return new ScheduledFutureWrapper(scheduledPool.scheduleAtFixedRate(r, delay, period, TimeUnit.MILLISECONDS));
	}
	
	public final ScheduledFuture<?> scheduleWithFixedDelay(Runnable r, long delay, long period) {
		r = new ThreadPoolExecuteWrapper(r);
//		delay = validate(delay);
//		period = validate(period);

		return new ScheduledFutureWrapper(scheduledPool.scheduleWithFixedDelay(r, delay, period, TimeUnit.MILLISECONDS));
	}

	// ===========================================================================================

	public final void execute(Runnable r) {
		r = new ThreadPoolExecuteWrapper(r);

		instantPool.execute(r);
	}


	// ===========================================================================================

	public final Future<?> submit(Runnable r) {
		r = new ThreadPoolExecuteWrapper(r);

		return instantPool.submit(r);
	}


	public void purge() {
		scheduledPool.purge();
		instantPool.purge();
	}

	/**
	 * Shutdown all thread pools.
	 */
	public void shutdown() {
		final long begin = System.currentTimeMillis();

		scheduledPool.shutdown();
		instantPool.shutdown();

		boolean success = false;
		try {
			success |= awaitTermination(5000);

			scheduledPool.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
			scheduledPool.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);

			success |= awaitTermination(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private int getTaskCount(ThreadPoolExecutor tp) {
		return tp.getQueue().size() + tp.getActiveCount();
	}

	public List<String> getStats() {
		List<String> list = new ArrayList<String>();

		list.add("");
		list.add("Scheduled pool:");
		list.add("=================================================");
		list.add("\tgetActiveCount: ...... " + scheduledPool.getActiveCount());
		list.add("\tgetCorePoolSize: ..... " + scheduledPool.getCorePoolSize());
		list.add("\tgetPoolSize: ......... " + scheduledPool.getPoolSize());
		list.add("\tgetLargestPoolSize: .. " + scheduledPool.getLargestPoolSize());
		list.add("\tgetMaximumPoolSize: .. " + scheduledPool.getMaximumPoolSize());
		list.add("\tgetCompletedTaskCount: " + scheduledPool.getCompletedTaskCount());
		list.add("\tgetQueuedTaskCount: .. " + scheduledPool.getQueue().size());
		list.add("\tgetTaskCount: ........ " + scheduledPool.getTaskCount());
		list.add("");
		list.add("Instant pool:");
		list.add("=================================================");
		list.add("\tgetActiveCount: ...... " + instantPool.getActiveCount());
		list.add("\tgetCorePoolSize: ..... " + instantPool.getCorePoolSize());
		list.add("\tgetPoolSize: ......... " + instantPool.getPoolSize());
		list.add("\tgetLargestPoolSize: .. " + instantPool.getLargestPoolSize());
		list.add("\tgetMaximumPoolSize: .. " + instantPool.getMaximumPoolSize());
		list.add("\tgetCompletedTaskCount: " + instantPool.getCompletedTaskCount());
		list.add("\tgetQueuedTaskCount: .. " + instantPool.getQueue().size());
		list.add("\tgetTaskCount: ........ " + instantPool.getTaskCount());
		list.add("");

		return list;
	}

	private boolean awaitTermination(long timeoutInMillisec) throws InterruptedException {
		final long begin = System.currentTimeMillis();

		while (System.currentTimeMillis() - begin < timeoutInMillisec) {
			if (!scheduledPool.awaitTermination(10, TimeUnit.MILLISECONDS) && scheduledPool.getActiveCount() > 0)
				continue;

			if (!instantPool.awaitTermination(10, TimeUnit.MILLISECONDS) && instantPool.getActiveCount() > 0)
				continue;


			return true;
		}

		return false;
	}
}
