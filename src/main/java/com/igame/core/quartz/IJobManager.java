package com.igame.core.quartz;

import org.quartz.SchedulerException;

import java.text.ParseException;
import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public interface IJobManager
{
  void initJobTrigger(List<MyJob> paramList)
    throws SchedulerException, ParseException;
}