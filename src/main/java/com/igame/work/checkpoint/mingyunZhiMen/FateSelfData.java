package com.igame.work.checkpoint.mingyunZhiMen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.List;


/**
 * 
 * @author Marcus.Z
 *命运之门数据
 */
@Entity(noClassnameStored = true)
public class FateSelfData {
	
	private int fateLevel = 1;//历史挑战到的命运之门层数
	
	@Transient
	private int todayFateLevel = 1;//当前挑战到的命运之门层数
	
	private int getReward;//今天已经领取宝箱次数 大于等于1不可再挑战
	
	private int todayBoxCount;//当前获得的最多命运之门宝箱数
	
	@JsonIgnore
	private int first;//是否首次进入过0 -首次  1-已结不是首次
	
	@Transient
	private int tempBoxCount = -1;//临时获得的命运之门宝箱数
	
	@JsonIgnore
	@Transient
	private int tempSpecialCount;//当前已随机特殊门的次数
	
	@JsonIgnore
	@Transient
	private int addRate;//增长额概率
	
	@JsonIgnore
	@Transient
	private List<GateDto> gate = Lists.newArrayList();
	
	@JsonIgnore
	@Transient
	private int gateId;

	public int getFateLevel() {
		return fateLevel;
	}

	public void setFateLevel(int fateLevel) {
		this.fateLevel = fateLevel;
	}

	public int getTodayFateLevel() {
		return todayFateLevel;
	}

	public void setTodayFateLevel(int todayFateLevel) {
		this.todayFateLevel = todayFateLevel;
	}
	
	public void addTodayFateLevel() {
		this.todayFateLevel += 1;
	}

	public int getTodayBoxCount() {
		return todayBoxCount;
	}

	public void setTodayBoxCount(int todayBoxCount) {
		this.todayBoxCount = todayBoxCount;
	}

	public int getTempBoxCount() {
		return tempBoxCount;
	}

	public void setTempBoxCount(int tempBoxCount) {
		this.tempBoxCount = tempBoxCount;
	}
	
	public void addTempBoxCount(int value) {
		if(this.tempBoxCount < 0){
			this.tempBoxCount = 0;
		}
		this.tempBoxCount += value;
		if(this.tempBoxCount > this.todayBoxCount){
			this.todayBoxCount = this.tempBoxCount;
		}
	}

	public int getGetReward() {
		return getReward;
	}

	public void setGetReward(int getReward) {
		this.getReward = getReward;
	}

	public int getTempSpecialCount() {
		return tempSpecialCount;
	}

	public void setTempSpecialCount(int tempSpecialCount) {
		this.tempSpecialCount = tempSpecialCount;
	}

	public int getAddRate() {
		return addRate;
	}

	public void setAddRate(int addRate) {
		this.addRate = addRate;
	}

	public int getFirst() {
		return first;
	}

	public void setFirst(int first) {
		this.first = first;
	}

	public List<GateDto> getGate() {
		return gate;
	}

	public void setGate(List<GateDto> gate) {
		this.gate = gate;
	}
	
	public GateDto getGate(int id){
		for(GateDto gt : gate){
			if(gt.getGateId() == id){
				return gt;
			}
		}
		return null;
	}

	public int getGateId() {
		return gateId;
	}

	public void setGateId(int gateId) {
		this.gateId = gateId;
	}
	

}
