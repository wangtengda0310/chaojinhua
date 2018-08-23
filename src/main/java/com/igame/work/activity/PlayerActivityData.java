package com.igame.work.activity;

import com.igame.work.activity.meiriLiangfa.MeiriLiangfaData;
import com.igame.work.activity.sign.SignData;
import org.mongodb.morphia.annotations.Entity;

@Entity(value = "activityData", noClassnameStored = true)
public class PlayerActivityData {
    private SignData sign;
    private MeiriLiangfaData meiriLiangfa;

    public PlayerActivityData() {

    }

    public SignData getSign() {
        return sign;
    }

    public void setSign(SignData sign) {
        this.sign = sign;
    }

    public MeiriLiangfaData getMeiriLiangfa() {
        return meiriLiangfa;
    }

    public void setMeiriLiangfa(MeiriLiangfaData meiriLiangfa) {
        this.meiriLiangfa = meiriLiangfa;
    }
}
