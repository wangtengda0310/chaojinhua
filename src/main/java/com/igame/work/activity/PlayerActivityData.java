package com.igame.work.activity;

import com.igame.work.user.dto.Player;
import org.mongodb.morphia.annotations.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Entity(value = "activityData", noClassnameStored = true)
public class PlayerActivityData {

    private List<Activities> activities = new ArrayList<>();

    public PlayerActivityData() {

    }

    public PlayerActivityData(Player player) {
        all().forEach(activity->activity.setPlayer(player));
    }

    public Stream<Activities> all() {
        return activities.stream();
    }

    public void add(Activities activity) {
        activities.add(activity);
    }
}
