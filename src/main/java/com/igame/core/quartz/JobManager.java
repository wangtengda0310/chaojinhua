package com.igame.core.quartz;


import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import com.igame.core.log.ExceptionLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * @author Marcus.Z
 *
 */
public class JobManager implements IJobManager{
    private static JobManager domain = new JobManager();

    public static final JobManager ins(){
        return domain;
    }

    private static SchedulerFactory schedulerFactory;
    private static Scheduler scheduler;


      public JobManager(){
          List<MyJob> jobList = initJobList();
          initJobTrigger(jobList);
      }


    private List<MyJob> initJobList(){
        List<MyJob> jobList = new ArrayList<MyJob>();
        AtomicInteger atomicInteger = new AtomicInteger(1);

        String c = "com.igame.core.quartz.GameQuartzListener";
//        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.seconds", c, "seconds", "0/1 * *  * * ?"));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.minute", c, "minute", "0 0/1 * * * ?"));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.minute5", c, "minute5", "0 0/5 * * * ?"));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.minute180", c, "minute180", "0 0 0/3 * * ? "));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.zero", c, "zero", "0 0 12 * * ?"));

        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.nine", c, "nine", "0 0 9 * * ?"));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.twelve", c, "twelve", "0 0 12 * * ?"));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.fourteen", c, "fourteen", "0 0 14 * * ?"));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.twenty", c, "twenty", "0 0 20 * * ?"));
        jobList.add(new MyJob(atomicInteger.incrementAndGet(), "job.week7", c, "week7", "0 0 12 ? * 2"));
        return jobList;
    }

    public void initJobTrigger(List<MyJob> jobList){
        try {
            schedulerFactory = new StdSchedulerFactory();
            scheduler = schedulerFactory.getScheduler();

            for (MyJob job : jobList) {
                JobDetail tokenJob = new JobDetail(job.jobId, "DEFAULT", JobExecutor.class);
                JobDataMap dataMap = new JobDataMap();
                dataMap.put("JOB_INFO", job);
                CronTrigger conTrigger = new CronTrigger(job.triggerId, "DEFAULT");
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