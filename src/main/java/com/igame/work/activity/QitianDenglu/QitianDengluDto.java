package com.igame.work.activity.QitianDenglu;

import com.igame.core.db.BasicDto;

public class QitianDengluDto extends BasicDto {
    private int activityId;
    private long playerId;
    private int loginTimes;

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public int getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(int loginTimes) {
        this.loginTimes = loginTimes;
    }
}
