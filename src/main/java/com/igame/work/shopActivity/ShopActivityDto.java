package com.igame.work.shopActivity;

import com.igame.core.db.BasicDto;

import java.util.HashMap;
import java.util.Map;

public class ShopActivityDto extends BasicDto {

    public int activityId;

    public Map<Long, ShopActivityPlayerDto> players = new HashMap<>();

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }
}