package com.igame.work.activity;

import org.mongodb.morphia.annotations.Entity;

import java.util.Map;

@Entity(noClassnameStored = true)
public class ActivityDto {
    private int activityId; // activityId 在外层 方便删除不存在活动的数据
    protected Map<Long, ActivityOrderDto> orderData;   // key playerId

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public Map<Long, ActivityOrderDto> getOrderData() {
        return orderData;
    }

    public void setOrderData(Map<Long, ActivityOrderDto> orderData) {
        this.orderData = orderData;
    }
}
