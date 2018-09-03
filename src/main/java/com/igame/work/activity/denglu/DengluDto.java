package com.igame.work.activity.denglu;

import com.igame.core.db.BasicDto;
import org.mongodb.morphia.annotations.Entity;

@Entity(noClassnameStored = true)
public class DengluDto extends BasicDto {
    private long playerId;
    private int activityId;
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

    public int[] getRecord() {
        return record;
    }

    public void setRecord(int[] record) {
        this.record = record;
    }
}
