package com.igame.work.chat.dto;

import com.igame.work.user.dto.Player;

/**
 *
 * @author xym
 *
 * 角色信息vo
 */
public class PlayerInfo {

    private long userId;

    private long playerId;

    private String name = "";

    private int playerFrameId;//玩家头像框

    private int playerHeadId;//玩家头像

    private int playerLevel;//玩家等级

    private long fightValue;

    public PlayerInfo() {
    }

    public PlayerInfo(Player player) {
        this.userId = player.getUserId();
        this.playerId = player.getPlayerId();
        this.name = player.getNickname();
        this.playerFrameId = player.getPlayerFrameId();
        this.playerHeadId = player.getPlayerHeadId();
        this.playerLevel = player.getPlayerLevel();
        this.fightValue = player.getFightValue();
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayerFrameId() {
        return playerFrameId;
    }

    public void setPlayerFrameId(int playerFrameId) {
        this.playerFrameId = playerFrameId;
    }

    public int getPlayerHeadId() {
        return playerHeadId;
    }

    public void setPlayerHeadId(int playerHeadId) {
        this.playerHeadId = playerHeadId;
    }

    public int getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(int playerLevel) {
        this.playerLevel = playerLevel;
    }

    public long getFightValue() {
        return fightValue;
    }

    public void setFightValue(long fightValue) {
        this.fightValue = fightValue;
    }
}