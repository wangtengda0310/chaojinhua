package com.igame.util;




/**
 * @author zhh
 *
 */
public class ThreadConfig
{	


	public static int BASE_THREAD_POOL_SIZE = 2;
	
	public static int EXTRA_THREAD_PER_CORE = 4;
	
	public static long MAXIMUM_RUNTIME_IN_MILLISEC_WITHOUT_WARNING = 5000;
	
	public static int THREAD_POOL_SIZE = 4;
	
	public static void load()
	{	

		final int baseThreadPoolSize = BASE_THREAD_POOL_SIZE;
		final int extraThreadPerCore = EXTRA_THREAD_PER_CORE;
		THREAD_POOL_SIZE = baseThreadPoolSize + Runtime.getRuntime().availableProcessors() * extraThreadPerCore;
	
	}
}
