package com.igame.dto;

/**
 * 
 * @author Marcus.Z
 *
 */
public class DBSeqcLock {
    private static final int DEFAULT_SIZE = 100;
    private static Object[] locks = new Object[100];
    private static Object oo = new Object();
    

    public static Object getLock(int index) {
        if (index < 0 || index >= locks.length) {
            return oo;
        }
        return locks[index];
    }

    static {
        for (int i = 0; i < DEFAULT_SIZE; i++)
            locks[i] = new Object();
    }
}
