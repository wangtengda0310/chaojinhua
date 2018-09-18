package com.igame.util;

import java.util.concurrent.TimeUnit;

import com.igame.core.log.ExceptionLog;

/**
 * 线程执行时间监控
 * @author zhh
 *
 */
public class ExecuteWrapper implements Runnable {

	private final Runnable		runnable;

	public ExecuteWrapper(Runnable runnable)
	{
		this.runnable = runnable;
	}

	@Override
	public final void run()
	{
		ExecuteWrapper.execute(runnable, getMaximumRuntimeInMillisecWithoutWarning());
	}

	protected long getMaximumRuntimeInMillisecWithoutWarning()
	{
		return Long.MAX_VALUE;
	}

	public static void execute(Runnable runnable)
	{
		execute(runnable, Long.MAX_VALUE);
	}

	public static void execute(Runnable runnable, long maximumRuntimeInMillisecWithoutWarning)
	{
		long begin = System.nanoTime();

		try
		{
			runnable.run();
		}
		catch(RuntimeException e)
		{
			ExceptionLog.error("Exception in a Runnable execution:");
		}
		finally
		{
			long runtimeInNanosec = System.nanoTime() - begin;
			Class<? extends Runnable> clazz = runnable.getClass();

			RunnableStatsManager.handleStats(clazz, runtimeInNanosec);

			long runtimeInMillisec = TimeUnit.NANOSECONDS.toMillis(runtimeInNanosec);

			if(runtimeInMillisec > maximumRuntimeInMillisecWithoutWarning)
			{
				ExceptionLog.error("maximumRuntimeInMillisecWithoutWarning in a Runnable execution:");
			}
		}
	}
}
