package com.igame.core.quartz;


import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.igame.core.log.ExceptionLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 
 * @author Marcus.Z
 *
 */
public class JobExecutor implements Job {
	private static Map<String, Method> cacheMap = new ConcurrentHashMap<String, Method>();

	public void execute(JobExecutionContext je) throws JobExecutionException {
		try {
			MyJob job = (MyJob) je.getTrigger().getJobDataMap().get("JOB_INFO");
            Class c  = Class.forName(job.className);
            Object obj = c.newInstance();
			String key = getKey(job.className, job.methodName);
			Method method = cacheMap.get(key);
			if (null == method) {
				synchronized (cacheMap) {
					if (cacheMap.get(key) == null) {
						method = obj.getClass().getMethod(job.methodName);
						cacheMap.put(key, method);
					}
				}
			}
			method.invoke(obj);
		} catch (SecurityException e) {
			ExceptionLog.error("", e);
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			ExceptionLog.error("", e);
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			ExceptionLog.error("", e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			ExceptionLog.error("", e);
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			ExceptionLog.error("", e.getTargetException());
			e.printStackTrace();
		} catch (Exception e) {
			ExceptionLog.error("", e);
			e.printStackTrace();
		}
	}

	private String getKey(String className, String methodName) {
		return className + "_" + methodName;
	}
}