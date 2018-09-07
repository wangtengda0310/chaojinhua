package com.igame.work.activity.denglu;

import com.igame.core.db.BasicDto;
import org.mongodb.morphia.annotations.Entity;

@Entity(noClassnameStored = true)
public class DengluDto extends BasicDto {
    private long playerId;
    private int activityId;
    private String openTime;    // 策划修改时间后当做新活动，需要这里记录一下上次活动时间
    private int[] record;

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public int[] getRecord() {
        return record;
    }

    public void setRecord(int[] record) {
        this.record = record;
    }
}
