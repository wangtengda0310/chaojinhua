package com.igame.work.checkpoint.dto;

/**
 * 
 * @author Marcus.Z
 *
 */
public class FateDto {
	
	private int fateLevel = 1;//今天可直达门数
	
	private int getReward;//今天已经领取宝箱次数 大于等于1不可再挑战
	
	private int todayBoxCount;//当前获得的最多宝箱数

	public int getFateLevel() {
		return fateLevel;
	}

	public void setFateLevel(int fateLevel) {
		this.fateLevel = fateLevel;
	}

	public int getGetReward() {
		return getReward;
	}

	public void setGetReward(int getReward) {
		this.getReward = getReward;
	}

	public int getTodayBoxCount() {
		return todayBoxCount;
	}

	public void setTodayBoxCount(int todayBoxCount) {
		this.todayBoxCount = todayBoxCount;
	}
	
	public static FateDto creatFateDto(FateSelfData fd){
		FateDto fo = new FateDto();
		fo.setFateLevel(fd.getFateLevel());
		fo.setGetReward(fd.getGetReward());
		fo.setTodayBoxCount(fd.getTodayBoxCount());
		return fo;
	}
	

}
