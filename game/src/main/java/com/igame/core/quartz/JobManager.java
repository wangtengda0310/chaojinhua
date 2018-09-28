package com.igame.core.quartz;


import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.log.ExceptionLog;
import com.igame.util.DateUtil;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * @author Marcus.Z
 *
 */
public class JobManager implements ISFSModule {

    @Inject private GameQuartzListener listener;

    private static Scheduler scheduler;

    static HashMap<String, TimeListener> listeners = new HashMap<>();

    public static void addJobListener(TimeListener listener) {
        listeners.put(listener.getClass().getSimpleName(), listener);
    }
    public void destroy() {
        try {
            scheduler.shutdown(true);
        } catch (SchedulerException e) {
            extensionHolder.SFSExtension.getLogger().error("",e);
        }
        listeners.clear();
    }

    @Override
      public void init(){
        try {
            scheduler = new StdSchedulerFactory().getScheduler();

            for (Method method : TimeListener.class.getMethods()) {
                if(!method.isAnnotationPresent(Cron.class)) {
                    continue;
                }
                String cron = method.getAnnotation(Cron.class).value();

                String methodName = method.getName();
                String id = DateUtil.formatClientDateTime(new Date());

                CronTrigger conTrigger = new CronTrigger("QuartzTrigger_"+ methodName+id, "DEFAULT");   // smart fox server热加载时重名trigger会冲突
                JobDataMap dataMap = new JobDataMap();
                dataMap.put("JOB_INSTANCE", listener);
                dataMap.put("JOB_METHOD", methodName);
                conTrigger.setJobDataMap(dataMap);
                conTrigger.setCronExpression(cron);
                scheduler.scheduleJob(
                        new JobDetail("QuartzJOB_"+ methodName + "_init_at_" + id   // smart fox server热加载时重名任务会冲突
                                , "DEFAULT", JobExecutor.class)
                        , conTrigger);
            }

            scheduler.start();
        }catch(Exception e){
            ExceptionLog.error("",e);
        }
      }

}