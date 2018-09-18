package com.igame.core.log;



import java.io.PrintWriter;
import java.io.StringWriter;





import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.igame.util.TimeUtil;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ExceptionLog {
    private static Logger log = LogManager.getLogger(ExceptionLog.class);

    public static void info(String msg) {
        log.info(TimeUtil.getCurFullData()+msg);
    }

    public static void error(Object msg) {
        log.error(TimeUtil.getCurFullData()+ " " +msg);
    }

    public static void error(Object msg, Throwable e) {
        log.error(TimeUtil.getCurFullData()+ " " +msg+ " " +getStackTrace(e));
    }

    public static void logCallStack(Throwable e, String baseStr) {
        String str = TimeUtil.getCurFullData() + baseStr + "#"+e.getMessage()+","+e;
        log.error(str+e);

        String fullStackTrace = getStackTrace(e);
        log.error(fullStackTrace);

        e.printStackTrace();
    }

    private static String getStackTrace(Throwable throwable){
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
    public static void print(Object msg){
        System.out.println(msg);
    }
}
