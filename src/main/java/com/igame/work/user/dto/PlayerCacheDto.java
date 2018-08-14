package com.igame.work.user.dto;

import com.google.common.collect.Lists;
import com.igame.core.db.BasicVO;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.friend.dto.FriendInfo;

import java.util.Date;
import java.util.List;

/**
 * 
 * @author Marcus.Z
 *玩家常用数据缓存对象
 */

public class PlayerCacheDto extends BasicVO {
	

	private long userId;//账号ID
	
	private long playerId;
	
	private int severId;

	private String name = "";
	
	private int gender;//玩家性别

    private int playerFrameId;//玩家头像框

	private int playerHeadId;//玩家头像

	private int playerLevel;//玩家等级

	private int vip;//VIP

	private long fightValue;
	
	private GodsDto  gods = new GodsDto();//无尽之森当前神灵
	
	private List<MatchMonsterDto> mon = Lists.newArrayList();

	private int curFriendCount;//当前好友数量

	private int maxFriendCount;//最大好友数量

	private Date lastLoginOutDate;//上回登出时间

	public PlayerCacheDto() {
	}

	public PlayerCacheDto(Player player) {

		this.userId = player.getUserId();
		this.playerId = player.getPlayerId();
		this.severId = player.getSeverId();
		this.name = player.getNickname();
		this.gender = player.getGender();
		this.playerFrameId = player.getPlayerFrameId();
		this.playerHeadId = player.getPlayerHeadId();
		this.playerLevel = player.getPlayerLevel();
		this.vip = player.getVip();
		this.fightValue = player.getFightValue();
		this.lastLoginOutDate = player.getLoginoutTime();
		this.curFriendCount = player.getFriends().getCurFriends().size();
		this.maxFriendCount = player.getPlayerTop().getFriendCount();
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

	public GodsDto getGods() {
		return gods;
	}

	public void setGods(GodsDto gods) {
		this.gods = gods;
	}

	public List<MatchMonsterDto> getMon() {
		return mon;
	}

	public void setMon(List<MatchMonsterDto> mon) {
		this.mon = mon;
	}

	public int getSeverId() {
		return severId;
	}

	public void setSeverId(int severId) {
		this.severId = severId;
	}

	public long getFightValue() {
		return fightValue;
	}

	public void setFightValue(long fightValue) {
		this.fightValue = fightValue;
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

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getPlayerLevel() {
		return playerLevel;
	}

	public void setPlayerLevel(int playerLevel) {
		this.playerLevel = playerLevel;
	}

	public int getVip() {
		return vip;
	}

	public void setVip(int vip) {
		this.vip = vip;
	}

	public int getCurFriendCount() {
		return curFriendCount;
	}

	public void setCurFriendCount(int curFriendCount) {
		this.curFriendCount = curFriendCount;
	}

	public int getMaxFriendCount() {
		return maxFriendCount;
	}

	public void setMaxFriendCount(int maxFriendCount) {
		this.maxFriendCount = maxFriendCount;
	}

	public Date getLastLoginOutDate() {
		return lastLoginOutDate == null? new Date() : lastLoginOutDate;
	}

	public void setLastLoginOutDate(Date lastLoginOutDate) {
		this.lastLoginOutDate = lastLoginOutDate;
	}

	public void updatePlayer(Player player){
		
		this.userId = player.getUserId();
		this.playerId = player.getPlayerId();
		this.severId = player.getSeverId();
		this.name = player.getNickname();
		this.gender = player.getGender();
		this.playerFrameId = player.getPlayerFrameId();
		this.playerHeadId = player.getPlayerHeadId();
		this.playerLevel = player.getPlayerLevel();
		this.vip = player.getVip();
		this.fightValue = player.getFightValue();
		this.lastLoginOutDate = player.getLoginoutTime();

		this.curFriendCount = player.getFriends().getCurFriends().size();
		if (player.getPlayerTop() == null){
			player.setPlayerTop(new PlayerTop().init());
		}
		this.maxFriendCount = player.getPlayerTop().getFriendCount();
	}


	public synchronized void addCurFriendCount(int value) {
		this.curFriendCount += value;
	}
}
