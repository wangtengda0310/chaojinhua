package com.igame.core.log;


import java.time.LocalDateTime;

/**
 * 
 * @author Marcus.Z
 *
 */
public class DebugLog {
    public static void info(Object data){
        debug(data);
    }
    public static void debug(Object data){
        System.out.println(LocalDateTime.now() + " DEBUG " + data);
    }
}
