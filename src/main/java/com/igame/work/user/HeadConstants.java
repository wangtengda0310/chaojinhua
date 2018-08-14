package com.igame.work.user;

/**
 * @author xym
 *
 * 头像、头像框常量
 */
public class HeadConstants {

    //头像触发解锁条件
    public final static int HEAD_TOUCH_NULL = -1;  //无
    public final static int HEAD_TOUCH_LV = 1;  //等级
    public final static int HEAD_TOUCH_MONSTER = 2; //怪兽
    public final static int HEAD_TOUCH_EVENT = 3;   //活动

    //头像框触发解锁条件
    public final static int FRAME_TOUCH_LV = 1; //等级
    public final static int FRAME_TOUCH_ARENA = 2;  //竞技场
    public final static int FRAME_TOUCH_PVP = 3;   //PVP组别
    public final static int FRAME_TOUCH_VIP = 4;   //VIP等级
    public final static int FRAME_TOUCH_EVENT = 5;   //特殊活动
}
