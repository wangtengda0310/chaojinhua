package com.igame.work.friend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
import org.mongodb.morphia.annotations.Transient;

import java.util.Date;

public class Friend{

	private long userId;//账号ID

	private long playerId;	//好友id

	@Transient
	private String nickName;	//玩家昵称

	@Transient
	private int playerLevel;	//玩家等级

	@Transient
	private int playerHeadId;	//玩家头像

	@Transient
	private int playerFrameId;	//玩家头像框

	@Transient
	private long fightValue;	//战力

	private int givePhy;	//体力赠送 0=未赠送，1=已赠送

	private int receivePhy;	//体力领取 0=未赠送，1=已赠送未领取，2=已领取

	@JsonIgnore
	private Date givePhyDate;	//对方何时赠送自己体力

	private int helpAcc;

	@Transient
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", locale = "zh", timezone = "GMT+8")
	private Date loginoutTime;	//离线时间

	public Friend() {
	}

	public Friend(Player player) {

		this.userId = player.getUserId();	//账号ID

		this.playerId = player.getPlayerId();	//好友id

		this.playerLevel = player.getPlayerLevel();	//玩家等级

		this.nickName = player.getNickname();	//玩家昵称

		this.playerFrameId = player.getPlayerFrameId();	//玩家头像框

		this.playerHeadId = player.getPlayerHeadId();	//玩家头像

		this.fightValue = player.getFightValue();	//战力

		this.loginoutTime = player.getLoginoutTime();	//离线时间

		this.givePhy = 0;	//体力赠送 0=未赠送，1=已赠送

		this.receivePhy = 0;	//体力领取 0=未赠送，1=已赠送未领取，2=已领取

		this.helpAcc = 0;
	}

	public void loadCache(Friend friend, int serverId) {

		Player cacheDto = PlayerCacheService.ins().getPlayerById(friend.getPlayerId());

		if(cacheDto!=null) {
			friend.setPlayerLevel(cacheDto.getPlayerLevel());    //玩家等级
			friend.setNickName(cacheDto.getNickname());    //玩家昵称
			friend.setPlayerFrameId(cacheDto.getPlayerFrameId());    //玩家头像框
			friend.setPlayerHeadId(cacheDto.getPlayerHeadId());    //玩家头像
			friend.setFightValue(cacheDto.getFightValue());    //战力
			friend.setLoginoutTime(cacheDto.getLoginoutTime());    //战力
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Friend) {
			Friend target = (Friend) o;
			return target.getPlayerId() == this.playerId;
		}else if (o instanceof Long){
			return (Long) o == this.playerId;
		}
		return false;
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

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getPlayerLevel() {
		return playerLevel;
	}

	public void setPlayerLevel(int playerLevel) {
		this.playerLevel = playerLevel;
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

	public long getFightValue() {
		return fightValue;
	}

	public void setFightValue(long fightValue) {
		this.fightValue = fightValue;
	}

	public int getGivePhy() {
		return givePhy;
	}

	public void setGivePhy(int givePhy) {
		this.givePhy = givePhy;
	}

	public int getReceivePhy() {
		return receivePhy;
	}

	public void setReceivePhy(int receivePhy) {
		this.receivePhy = receivePhy;
	}

	public Date getGivePhyDate() {
		return givePhyDate;
	}

	public void setGivePhyDate(Date givePhyDate) {
		this.givePhyDate = givePhyDate;
	}

	public Date getLoginoutTime() {
		return loginoutTime;
	}

	public void setLoginoutTime(Date loginoutTime) {
		this.loginoutTime = loginoutTime;
	}

	public int getHelpAcc() {
		return helpAcc;
	}

	public void setHelpAcc(int helpAcc) {
		this.helpAcc = helpAcc;
	}

	@Override
	public String toString() {
		return "Friend{" +
				"userId=" + userId +
				", playerId=" + playerId +
				", givePhy=" + givePhy +
				", receivePhy=" + receivePhy +
				", givePhyDate=" + givePhyDate +
				'}';
	}
}