package com.igame.core.quartz;


import com.igame.core.ISFSModule;
import com.igame.core.log.ExceptionLog;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Marcus.Z
 *
 */
public class JobManager implements ISFSModule {

    private static SchedulerFactory schedulerFactory;
    private static Scheduler scheduler;

    static HashMap<String, TimeListener> listeners = new HashMap<>();

    public static void addJobListener(TimeListener listener) {
        listeners.put(listener.getClass().getSimpleName(), listener);
    }
    public void destroy() {
        listeners.clear();
    }

    @Override
      public void init(){
        try {
            List<MyJob> jobList = new ArrayList<>();
            AtomicInteger atomicInteger = new AtomicInteger(1);

            String className = "com.igame.core.quartz.GameQuartzListener";
//        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.seconds", className, "seconds", "0/1 * *  * * ?"));
            jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.minute", className, "minute", "0 0/1 * * * ?"));
            jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.minute5", className, "minute5", "0 0/5 * * * ?"));
            jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.minute180", className, "minute180", "0 0 0/3 * * ? "));
            jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.zero", className, "zero", "0 0 12 * * ?"));

            jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.nine", className, "nine", "0 0 9 * * ?"));
            jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.twelve", className, "twelve", "0 0 12 * * ?"));
            jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.fourteen", className, "fourteen", "0 0 14 * * ?"));
            jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.twenty", className, "twenty", "0 0 20 * * ?"));
            jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.week7", className, "week7", "0 0 12 ? * 2"));

            schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();

            for (MyJob job : jobList) {
                JobDetail tokenJob = new JobDetail(job.jobId, "DEFAULT", JobExecutor.class);
                CronTrigger conTrigger = new CronTrigger(job.triggerId, "DEFAULT");
                JobDataMap dataMap = new JobDataMap();
                dataMap.put("JOB_INFO", job);
                conTrigger.setJobDataMap(dataMap);
                conTrigger.setCronExpression(job.cronExpression);
                scheduler.scheduleJob(tokenJob, conTrigger);
            }
            scheduler.start();
        }catch(Exception e){
            ExceptionLog.error("",e);
        }
      }

}