package com.igame.work.checkpoint.xingheZhiYan;



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
@XmlRootElement(name = "item")
public class TrialdataTemplate {
	

	@XmlAttribute(name = "num")
	private int num;
		
	@XmlAttribute(name = "gold")
	private  int gold;

	@XmlAttribute(name = "monster_data")
	private int monsterData;
	
	@XmlAttribute(name = "reward")
	private  String reward;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getMonsterData() {
		return monsterData;
	}

	public void setMonsterData(int monsterData) {
		this.monsterData = monsterData;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}
}
