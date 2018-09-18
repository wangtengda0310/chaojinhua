package com.igame.work.checkpoint.xinmo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(noClassnameStored = true)
public class XingMoDto {
	
	
	private int checkPiontId;//
	
	@JsonIgnore
	private long playerId;//
	
	private String mid;//心魔ID，为空字符串时表示待改变和生成具体机器人数据
	
	@JsonIgnore
	private long statTime;
	
	@Transient
	private long leftTime;
	
	private int refCount;
	
	private int playerFrameId;//玩家头像框
	
	private int playerHeadId;//玩家头像
	
	private int type;//1-强化的
	
	

	public int getCheckPiontId() {
		return checkPiontId;
	}

	public void setCheckPiontId(int checkPiontId) {
		this.checkPiontId = checkPiontId;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public long getStatTime() {
		return statTime;
	}

	public void setStatTime(long statTime) {
		this.statTime = statTime;
	}

	public int getRefCount() {
		return refCount;
	}

	public void setRefCount(int refCount) {
		this.refCount = refCount;
	}

	public long getLeftTime() {
		return leftTime;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public void setLeftTime(long leftTime) {
		this.leftTime = leftTime;
	}
	
	
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public long calLeftTime(long now){
		if(statTime == 0){
			this.leftTime = 0;
		}else{
			this.leftTime = (statTime + 24 * 3600 * 1000) - now > 0 ? ((statTime + 24 * 3600 * 1000) - now)/1000 : 0;
		}
		return this.leftTime;
				
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
	

}
