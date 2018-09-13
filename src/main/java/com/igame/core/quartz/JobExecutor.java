package com.igame.core.quartz;


import com.igame.core.log.ExceptionLog;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author Marcus.Z
 *
 */
public class JobExecutor implements Job {

	public void execute(JobExecutionContext je) {
		try {
			Object obj = je.getTrigger().getJobDataMap().get("JOB_INSTANCE");
			String methodName = (String) je.getTrigger().getJobDataMap().get("JOB_METHOD");
			Method method = obj.getClass().getMethod(methodName);
			method.invoke(obj);
		} catch (SecurityException | NoSuchMethodException | IllegalArgumentException | IllegalAccessException e) {
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

}