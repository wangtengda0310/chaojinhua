package com.igame.work.activity;

import com.igame.work.activity.sign.SignData;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.annotations.Entity;

@Entity(value = "activityData", noClassnameStored = true)
public class PlayerActivityData {
    public PlayerActivityData(Player player){
        signData = new SignData(player);
    }

    public SignData getSignData() {
        return signData;
    }

    public void setSignData(SignData signData) {
        this.signData = signData;
    }

    private SignData signData;
}
