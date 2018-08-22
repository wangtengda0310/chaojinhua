package com.igame.work.activity;

import com.igame.work.user.dto.Player;

public class ActivityService {
    public static void loadPlayer(Player player) {
        if (player.getActivityData() == null) {
            player.setActivityData(new PlayerActivityData(player));
        } else {
            player.getActivityData().all().forEach(activity->activity.setPlayer(player));
        }
    }
}
