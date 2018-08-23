package com.igame.work.activity;

import com.igame.work.user.dto.Player;

public class ActivityService {
    public static void loadPlayer(Player player) {
        if (player.getActivityData() == null) {
            player.setActivityData(new PlayerActivityData());
        }
    }
}
