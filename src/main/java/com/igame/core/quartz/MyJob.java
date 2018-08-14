package com.igame.core.quartz;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MyJob
{
  public String jobId;
  public String triggerId;
  public String jobName;
  public String className;
  public String methodName;
  public String cronExpression;

  public MyJob(int jobId, String jobName, String className, String methodName, String cronExpression)
  {
    this.jobId = ("QuartzJOB_" + jobId);
    this.triggerId = ("QuartzTrigger_" + jobId);
    this.jobName = jobName;
    this.className = className;
    this.methodName = methodName;
    this.cronExpression = cronExpression;
  }
}