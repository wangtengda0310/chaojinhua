package com.igame.core.data.template;



import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class QuestTemplate {
	

	@XmlAttribute(name = "quest_id")
	private int questId;
	
	//1日常2成就3限时
	@XmlAttribute(name = "quest_type")
	private  int questType;
	
	@XmlAttribute(name = "rate")
	private  int rate;
	
	//任务限制(1玩家等级；2任务完成)
	@XmlAttribute(name = "unlock")
	private String unlock;
	
	//任务要求
	@XmlAttribute(name = "claim")
	private  int claim;
	
	//完成数
	@XmlAttribute(name = "finish")
	private  int finish;
	
	//任务奖励
	@XmlAttribute(name = "reward")
	private String reward;
	
	//限时时间
	@XmlAttribute(name = "limit_time")
	private  int limitTime;
	
	//刷新消耗
	@XmlAttribute(name = "reset")
	private  int reset;

	public int getQuestId() {
		return questId;
	}

	public void setQuestId(int questId) {
		this.questId = questId;
	}

	public int getQuestType() {
		return questType;
	}

	public void setQuestType(int questType) {
		this.questType = questType;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getUnlock() {
		return unlock;
	}

	public void setUnlock(String unlock) {
		this.unlock = unlock;
	}

	public int getClaim() {
		return claim;
	}

	public void setClaim(int claim) {
		this.claim = claim;
	}

	public int getFinish() {
		return finish;
	}

	public void setFinish(int finish) {
		this.finish = finish;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	public int getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(int limitTime) {
		this.limitTime = limitTime;
	}

	public int getReset() {
		return reset;
	}

	public void setReset(int reset) {
		this.reset = reset;
	}
	
	
	
	
	
	
	
	
	

}
