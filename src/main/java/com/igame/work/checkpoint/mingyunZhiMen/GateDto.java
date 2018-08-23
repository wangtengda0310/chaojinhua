package com.igame.work.checkpoint.mingyunZhiMen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.igame.work.monster.dto.Monster;

import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class GateDto {
	
	private int gateId;//门ID
	
	private int type; //0-普通怪物，1-BUFF,2-支援,3-BOSS
	
	private int level;//等级
	
	private String monsterId = "";//显示的怪物模板ID，逗号分隔（2个）
	
	private int boxCount;//宝箱个数
	
	@JsonIgnore
	private Map<Long,Monster> mons = Maps.newHashMap();

	public int getGateId() {
		return gateId;
	}

	public void setGateId(int gateId) {
		this.gateId = gateId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getBoxCount() {
		return boxCount;
	}

	public void setBoxCount(int boxCount) {
		this.boxCount = boxCount;
	}

	public String getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(String monsterId) {
		this.monsterId = monsterId;
	}

	public Map<Long, Monster> getMons() {
		return mons;
	}

	public void setMons(Map<Long, Monster> mons) {
		this.mons = mons;
	}
	
	


}
