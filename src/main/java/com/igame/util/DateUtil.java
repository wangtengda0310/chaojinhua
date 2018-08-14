package com.igame.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author zhh
 *
 */
public class DateUtil {
	
	private static final DateFormat mailFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private static final DateFormat tiemFormat = new SimpleDateFormat("HH:mm:ss");
	
	public static String formatMailDate(Date date){
		return mailFormat.format(date);
	}
	
	public static String formatTimeDate(Date date){
		return tiemFormat.format(date);
	}
	
	/**
	 * 返回的是毫秒
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static long getIntervalTime(Date date1,Date date2){
		return Math.abs(date1.getTime() - date2.getTime());
	}
	
	
	public static long getIntervalToSoftTime(long curtime)
	{
		long s_time = 60*60*1000;
		
		long time = curtime % s_time;
		if(time != 0)
			return s_time - time;
		
		return 0;
	}
	
	public static int mathTimeInterval(Timestamp firsttime,long lasttime)
	{
//		long s_time = 30*60*1000;
//		
//		
//		long delayTime_first = getIntervalToSoftTime(firsttime);
//		long delayTime_last = getIntervalToSoftTime(lasttime);
//		
//		long _firsttime = firsttime + delayTime_first;
//		long _lasttime = lasttime + delayTime_last;
//		
//		int interval = (int) ((_lasttime - _firsttime)/s_time);
		if(firsttime == null){
			return 1000;
		}
		int interval = (int)((lasttime - firsttime.getTime())/(1000 * 60 * 5));
		
		return interval;
	}
	
	public static  boolean isNeedFushDate(Date lastTime){
		if(lastTime != null){
			Calendar ll = Calendar.getInstance();
			Calendar now = Calendar.getInstance();
			Calendar tody = Calendar.getInstance();
			ll.setTime(lastTime);
			tody.set(Calendar.HOUR_OF_DAY, 0);
			tody.set(Calendar.MINUTE, 0);
			tody.set(Calendar.SECOND, 0);
			if(ll.before(tody) && now.after(tody)){
				return true;
			}
		}
		return false;
	}
	
	public static  boolean isNeedFushDate(Date lastTime,int hour){
		if(lastTime != null){
			Calendar last = Calendar.getInstance();
			Calendar now = Calendar.getInstance();
			Calendar biaozhun = Calendar.getInstance();
			last.setTime(lastTime);
			biaozhun.set(Calendar.HOUR_OF_DAY, hour);
			biaozhun.set(Calendar.MINUTE, 0);
			biaozhun.set(Calendar.SECOND, 0);
			biaozhun.set(Calendar.MILLISECOND, 0);
			if(last.before(biaozhun) && now.after(biaozhun)){
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args){
//		System.out.println(formatMailDate(new Date()));
		

		
	}

}
