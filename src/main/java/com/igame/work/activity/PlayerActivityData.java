package com.igame.work.activity;

import com.igame.work.activity.meiriLiangfa.MeiriLiangfaData;
import com.igame.work.activity.sign.SignData;
import com.igame.work.activity.tansuoZhiLu.TanSuoZhiLuActivityData;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.annotations.Entity;

@Entity(value = "activityData", noClassnameStored = true)
public class PlayerActivityData {
    private SignData sign;
    private MeiriLiangfaData meiriLiangfa;
    private TanSuoZhiLuActivityData tansuo;

    private Player player;

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

    public TanSuoZhiLuActivityData getTansuo() {
        return tansuo;
    }

    public void setTansuo(TanSuoZhiLuActivityData tansuo) {
        this.tansuo = tansuo;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
