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
	private String monsterData;
	
	@XmlAttribute(name = "monster_lv")
	private  String monsterLv;
	
	@XmlAttribute(name = "monster_skilllv")
	private  String monsterSkilllv;
	
	@XmlAttribute(name = "monster_prop")
	private  String monsterProp;
	
	@XmlAttribute(name = "checkReward")
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

	public String getMonsterData() {
		return monsterData;
	}

	public void setMonsterData(String monsterData) {
		this.monsterData = monsterData;
	}

	public String getMonsterLv() {
		return monsterLv;
	}

	public void setMonsterLv(String monsterLv) {
		this.monsterLv = monsterLv;
	}

	public String getMonsterSkilllv() {
		return monsterSkilllv;
	}

	public void setMonsterSkilllv(String monsterSkilllv) {
		this.monsterSkilllv = monsterSkilllv;
	}

	public String getMonsterProp() {
		return monsterProp;
	}

	public void setMonsterProp(String monsterProp) {
		this.monsterProp = monsterProp;
	}

	public String getReward() {
		return reward;
	}

	public void setReward(String reward) {
		this.reward = reward;
	}

	
	
	
	
	
	
	
	

}
