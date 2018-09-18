package com.igame.core.quartz;


import com.igame.core.log.ExceptionLog;
import com.igame.util.DateUtil;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.lang.reflect.Method;
import java.util.Date;

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
			ExceptionLog.error("execute "+method.getName()+" method @"+ DateUtil.formatDateTime(new Date()));
			method.invoke(obj);
		} catch (Exception e) {
			ExceptionLog.error("", e);
			e.printStackTrace();
		}
	}

}