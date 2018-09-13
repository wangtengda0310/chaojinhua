package com.igame.core.quartz;


import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.log.ExceptionLog;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.HashMap;
import java.util.Map;

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

            Map<String, String> methodCronExpression = new HashMap<>();
            methodCronExpression.put("second", "0/1 * *  * * ?");
            methodCronExpression.put("minute", "0 0/1 * * * ?");
            methodCronExpression.put("minute5", "0 0/5 * * * ?");
            methodCronExpression.put("minute180", "0 0 0/3 * * ? ");
            methodCronExpression.put("zero", "0 0 12 * * ?");

            methodCronExpression.put("nine", "0 0 9 * * ?");
            methodCronExpression.put("twelve", "0 0 12 * * ?");
            methodCronExpression.put("fourteen", "0 0 14 * * ?");
            methodCronExpression.put("twenty", "0 0 20 * * ?");
            methodCronExpression.put("week7", "0 0 12 ? * 2");

            for(String method: methodCronExpression.keySet()) {
                CronTrigger conTrigger = new CronTrigger("QuartzTrigger_"+method, "DEFAULT");
                JobDataMap dataMap = new JobDataMap();
                dataMap.put("JOB_INSTANCE", listener);
                dataMap.put("JOB_METHOD", method);
                conTrigger.setJobDataMap(dataMap);
                conTrigger.setCronExpression(methodCronExpression.get(method));
                scheduler.scheduleJob(new JobDetail("QuartzJOB_"+method, "DEFAULT", JobExecutor.class)
                        , conTrigger);
            }

            scheduler.start();
        }catch(Exception e){
            ExceptionLog.error("",e);
        }
      }

}