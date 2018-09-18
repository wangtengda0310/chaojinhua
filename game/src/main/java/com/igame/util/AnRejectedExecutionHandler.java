package com.igame.util;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import com.igame.core.log.ExceptionLog;


/**
 * 
 * @author zhh
 *
 */
public class AnRejectedExecutionHandler implements RejectedExecutionHandler
{

	@Override
	public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)
	{
		if(executor.isShutdown())
			return;

		ExceptionLog.error(r + " from " + executor, new RejectedExecutionException());

		if(Thread.currentThread().getPriority() > Thread.NORM_PRIORITY)
			new Thread(r).start();
		else
			r.run();
	}
}

