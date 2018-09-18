package com.igame.util;
import com.google.common.base.Strings;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 
 * @author Marcus.Z
 *
 */
public class TimeUtil {
    public static final int HOUR12 = 12*60*60*1000;

    public static final long ONE_DAY = 86400000L;

    public static final int HALF_HOUR_SECONDS = 1800;


    public static final int HALF_HOUR_MILLS = 1800000;


    public static final int HOUR_SECONDS = 3600;


    public static final int ONE_HOUR_MILLS = 3600000;

    public static final int ONE_MINUTE_MILLIS = 60000;


    public static final int MINUTE10_MILLIS = 600000;

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String SIMPLE_PATTERN = "yyyy-MM-dd";
    private static final String FULL_PATTERN = "yyyyMMddhhmmss";

    public static boolean isSameDay(Date start, Date end) {
        long startTime = start.getTime();
        long endTime = end.getTime();
        return isSameDay(startTime, endTime);
    }


    public static int getDayOfWeek(){
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK)-1;
    }

    public static String getFullData() {
        SimpleDateFormat format = new SimpleDateFormat(FULL_PATTERN);
        return format.format(new Date());
    }

    public static boolean isSameDay(long start, long end) {
        return end - start < ONE_DAY;
    }

    public static String convertDBDate(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat format = new SimpleDateFormat(PATTERN);
        return format.format(date);
    }

    public static String convertDBDate(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        SimpleDateFormat format = new SimpleDateFormat(PATTERN);
        return format.format(c.getTime());
    }



    public static String convertSimpleDate(Date date) {
        if (date == null)
            return "";
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_PATTERN);
        return format.format(date);
    }
    public static String convertSimpleDate(long mills) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mills);
        Date date = c.getTime();
        if (date == null)
            return "";
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_PATTERN);
        return format.format(date);
    }


    public static long getStartTime() {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        return start.getTimeInMillis();
    }

    public static long getTimeByHour(int hour) {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.HOUR_OF_DAY, hour);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        return start.getTimeInMillis();
    }

    public static long getEndTime() {
        Calendar end = Calendar.getInstance();
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        return end.getTimeInMillis();
    }

    /**
     * get remain diff hours
     * @param time
     * @return
     */
    public static int getRemainHour(long time){
        long now = System.currentTimeMillis();
        long remain = now - time;
        return (int)(remain/1000/60/60);
    }


    public static int getHalfHourNumFrom(long lastTime) {
        if (0L >= lastTime) {
            return 0;
        }
        long now = System.currentTimeMillis();
        if (now <= lastTime) {
            return 0;
        }

        Calendar nowCal = Calendar.getInstance();
        nowCal.setTimeInMillis(now);
        if (nowCal.get(Calendar.MINUTE) >= 30)
            nowCal.set(Calendar.MINUTE, 30);
        else {
            nowCal.set(Calendar.MINUTE, 0);
        }

        nowCal.set(Calendar.SECOND, 0);
        nowCal.set(Calendar.MILLISECOND, 0);

        Calendar lastCal = Calendar.getInstance();
        lastCal.setTimeInMillis(lastTime);
        if (lastCal.get(Calendar.MINUTE) >= 30)
            lastCal.set(Calendar.MINUTE, 30);
        else {
            lastCal.set(Calendar.MINUTE, 0);
        }

        lastCal.set(Calendar.SECOND, 0);
        lastCal.set(Calendar.MILLISECOND, 0);

        return (int) ((nowCal.getTimeInMillis() - lastCal.getTimeInMillis()) / 1000L / 60L / 30L);
    }

    public static int getPassHour(long lastTime, int n) {
        int all = getHalfHourNumFrom(lastTime);
        all /= 2;// �õ�1Сʱ��
        all /= n;// �õ�NСʱ��
        return all;
    }

    public static int getFiftyMinuteNum(long lastTime) {
        if (0L >= lastTime) {
            return 0;
        }
        long now = System.currentTimeMillis();
        if (now <= lastTime) {
            return 0;
        }

        Calendar nowCal = Calendar.getInstance();
        nowCal.setTimeInMillis(now);
        if (nowCal.get(Calendar.MINUTE) >= 15)
            nowCal.set(Calendar.MINUTE, 15);
        else {
            nowCal.set(Calendar.MINUTE, 0);
        }

        nowCal.set(Calendar.SECOND, 0);
        nowCal.set(Calendar.MILLISECOND, 0);

        Calendar lastCal = Calendar.getInstance();
        lastCal.setTimeInMillis(lastTime);
        if (lastCal.get(Calendar.MINUTE) >= 15)
            lastCal.set(Calendar.MINUTE, 15);
        else {
            lastCal.set(Calendar.MINUTE, 0);
        }

        lastCal.set(Calendar.SECOND, 0);
        lastCal.set(Calendar.MILLISECOND, 0);

        return (int) ((nowCal.getTimeInMillis() - lastCal.getTimeInMillis()) / 1000L / 60L / 15L);
    }



    /**
     * yyyy-mm-dd
     *
     * @param start
     * @param end
     * @return
     */
    public static boolean isInTime(String start, String end) {
        if (Strings.isNullOrEmpty(start) || Strings.isNullOrEmpty(end))
            return false;
        try {
            SimpleDateFormat format = new SimpleDateFormat(SIMPLE_PATTERN);
            Date s = format.parse(start);
            Date e = format.parse(end);
            Date now = new Date();
            return now.after(s) && now.before(e);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * YYYY-MM-DD
     *
     * @param date
     * @return
     */
    public static Date getDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_PATTERN);
        try {
            return format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date getShortDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_PATTERN);
        try {
            return format.parse(format.format(date));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCurFullData() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(PATTERN));
    }

    public static String getCurShortData() {
        return LocalDateTime.now().format(
                DateTimeFormatter.ofPattern(SIMPLE_PATTERN));
    }

    public static String getShortDataByMills(long mills) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(mills);
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_PATTERN);
        try {
            return format.format(c.getTime());
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * ����ת���ɷ���
     */
    public static int transitionMinutes(long millisecond) {
        int minutes = (int) (millisecond / 60000);
        int remainder = (int) (millisecond % 60000);
        if (remainder != 0) {
            return minutes + 1;
        }
        return minutes;
    }
}
