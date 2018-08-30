package com.igame.work.activity.tansuoZhiLu;

import com.igame.work.activity.ActivityConfigTemplate;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.ArrayList;
import java.util.List;

@Entity(noClassnameStored = true)
public class TanSuoZhiLuActivityData {
    private String receivedLevels;

    @Transient
    static List<ActivityConfigTemplate> configs = new ArrayList<>();
    public static void addActivityConfigTemplate(ActivityConfigTemplate template) {
        if (1005==template.getActivity_sign()) {
            configs.add(template);
        }
    }

    public String getReceivedLevels() {
        return receivedLevels;
    }

    public void setReceivedLevels(String receivedLevels) {
        this.receivedLevels = receivedLevels;
    }

    public String clientData(Player player) {
        if (receivedLevels == null|| "".equals(receivedLevels)) {
            receivedLevels = ",";
        }
        String[] levels = new String[configs.size()];
        for (ActivityConfigTemplate config : configs) {
            int index = config.getOrder() - 1;
            String levelLimit = config.getGet_value();
            if (player.getPlayerLevel() >= Integer.parseInt(levelLimit)) {
                if (receivedLevels.contains("," + levelLimit + ",")) {
                    levels[index] = "2";
                } else {
                    levels[index] = "1";
                }
            } else {
                levels[index] = "0";
            }
        }
        return String.join(",",levels);
    }
}
