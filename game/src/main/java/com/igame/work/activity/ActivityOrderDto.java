package com.igame.work.activity;

import java.util.Map;

public class ActivityOrderDto {
    public int[] state; // 逗号分隔的012
    /** key activityOrder value record*/
    public Map<Integer, Object> recrod;  // 需要记录的不同类型活动需要记录的数据 比如充值金额 消耗金币这种   // todo 领奖或过期的时候清掉
}
