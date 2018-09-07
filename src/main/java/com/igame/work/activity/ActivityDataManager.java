package com.igame.work.activity;

import com.igame.work.activity.sign.SignConfig;

public class ActivityDataManager {
    /**
     * 签到
     */
    public static SignConfig signConfig;
    /**
     * 大部分活动共用的配置
     */
    public static ActivityConfig activityConfig;    // todo 同一个活动 不同order的配置time_limit如果不同 给策划报个错
}
