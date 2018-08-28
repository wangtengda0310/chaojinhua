package com.igame.core.quartz;

public interface TimeListener {
    default void minute(){}
    default void minute5(){}
    default void minute180(){}
    default void zero(){}
    default void nine(){}
    default void twelve(){}
    default void fourteen(){}
    default void twenty(){}
    default void week7(){}
}
