package com.igame.work.fight.dto;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.igame.work.monster.dto.Effect;


/**
 * 
 * @author Marcus.Z
 *
 */
public class FightBase {
	
	public long winner = -1;
	
	public long id;
	
	public long startTime;
	
	public int type; //0-PVE 1-pvp
	
	public FightData fightA;
	
	public FightData fightB;
	
	public Set<Long> state = Sets.newHashSet();
	
	public Object lock = new Object();
	
	public List<Effect> scBuffers = Lists.newArrayList();//区域HOTBUFFER列表
	
	public List<Effect> areaList = Lists.newArrayList();//区域属性BUFFER列表
	
	public FightBase(){}
	
	

	public FightBase(long id, FightData fightA, FightData fightB) {
		this.id = id;
		this.fightA = fightA;
		this.fightB = fightB;
		this.startTime = System.currentTimeMillis();
	}



	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public FightData getFightA() {
		return fightA;
	}

	public void setFightA(FightData fightA) {
		this.fightA = fightA;
	}

	public FightData getFightB() {
		return fightB;
	}

	public void setFightB(FightData fightB) {
		this.fightB = fightB;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}



	public long getWinner() {
		return winner;
	}



	public void setWinner(long winner) {
		this.winner = winner;
	}



	public Object getLock() {
		return lock;
	}



	public int getType() {
		return type;
	}



	public void setType(int type) {
		this.type = type;
	}



	public List<Effect> getScBuffers() {
		return scBuffers;
	}



	public void setScBuffers(List<Effect> scBuffers) {
		this.scBuffers = scBuffers;
	}
	
	
	
	public List<Effect> getAreaList() {
		return areaList;
	}



	public void setAreaList(List<Effect> areaList) {
		this.areaList = areaList;
	}



	public Effect addEffectToHotList(Effect effect){
		if(effect.getRepeat() <= 1){//替换
			Iterator<Effect> efs = this.scBuffers.iterator();
			while(efs.hasNext()){
				Effect ef = efs.next();
				if(ef.getEffectId() == effect.getEffectId()){
					efs.remove();
				}
			}
			this.scBuffers.add(effect);
			return effect;
		}else{
			int count = 0;
			for(Effect ef : this.scBuffers){
				if(ef.getEffectId() == effect.getEffectId()){
					count++;
				}
			}
			if(count < effect.getRepeat()){
				this.scBuffers.add(effect);
				return effect;
			}else{
				return null;
			}
		}
	}
	
	
	
	

}
