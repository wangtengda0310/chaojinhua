package com.igame.work.vip;

import org.mongodb.morphia.annotations.Entity;

@Entity(noClassnameStored = true)
public class VipDto {
    public long playerId;
    public String lastDailyPack;    // 最后一次领取每日礼包的日期
    public int[] firstPack;           // 已经领取的特权礼包
}
