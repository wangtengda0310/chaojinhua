package com.igame.work.user.dto;

import java.util.List;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.igame.core.db.BasicDto;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(value = "Robot", noClassnameStored = true)
public class RobotDto   extends BasicDto {
	
	@JsonIgnore
	private int severId;
	
	private long playerId;
	
	private String name;
	
	private int level;
	
	@JsonIgnore
	private long fightValue;
	
	private GodsDto  gods = new GodsDto();//无尽之森当前神灵
	
	private List<MatchMonsterDto> mon = Lists.newArrayList();
	
	@Transient
	@JsonIgnore
	public int dtate;//数据库状态 0-NO 1-新增 2-更新 3-删除
	
	private int playerFrameId;//玩家头像框
	
	private int playerHeadId;//玩家头像
	
	@JsonIgnore
	private int type= 0;

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

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
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
	
	public void setDtate(int ndtate) {
		switch (ndtate) {
			case 1://添加 
				if (this.dtate == 2 || this.dtate == 3)
					this.dtate = 2;
				else
					this.dtate = 1;
				break;
			case 2://更新
				if(this.dtate == 1){
					this.dtate = 1;
				} else{
					this.dtate = 2;
				}
				break;
			case 3://删除
				if (this.dtate == 1)
					this.dtate = 0;
				else {
					this.dtate = 3;
				}
				break;
			default:
				this.dtate = ndtate;
				break;
		}
	}

	public int getSeverId() {
		return severId;
	}

	public void setSeverId(int severId) {
		this.severId = severId;
	}

	public int getDtate() {
		return dtate;
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

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	

}
