package com.igame.work.user.dto;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ResCdto {
	
	public int chapterId;
	
	public String reward;
	
	public ResCdto(){}

	public ResCdto(int chapterId, String reward) {
		super();
		this.chapterId = chapterId;
		this.reward = reward;
	}

	public int getChapterId() {
		return chapterId;
	}

	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}
	
	

}
