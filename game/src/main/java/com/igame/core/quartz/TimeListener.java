package com.igame.core.quartz;

public interface TimeListener {
    @Cron("0 0/1 * * * ?")
    default void minute(){}
    @Cron("0 0/5 * * * ?")
    default void minute5(){}
    @Cron("0 0 0/3 * * ? ")
    default void minute180(){}
    @Cron("0 0 12 * * ?")
    default void zero(){}
    @Cron("0 0 9 * * ?")
    default void nine(){}
    @Cron("0 0 12 * * ?")
    default void twelve(){}
    @Cron("0 0 14 * * ?")
    default void fourteen(){}
    @Cron("0 0 20 * * ?")
    default void twenty(){}
    @Cron("0 0 12 ? * 2")
    default void week7(){}
}
