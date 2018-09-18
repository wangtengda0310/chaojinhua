package com.igame.work.activitylimit;

import com.igame.core.db.BasicDto;
import org.mongodb.morphia.annotations.Entity;

import java.util.HashMap;
import java.util.Map;

@Entity(noClassnameStored = true)
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