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
public class EndlessdataTemplate {
	

	@XmlAttribute(name = "num")
	private int num;
		

	@XmlAttribute(name = "lv_range")
	private String lvRange;
	
	@XmlAttribute(name = "difficulty")
	private  int difficulty;
	
	@XmlAttribute(name = "monster_lv")
	private  String monsterLv;
	
	@XmlAttribute(name = "monster_id")
	private  String monsterId;
	
	@XmlAttribute(name = "monster_prop")
	private  String monsterProp;
	
	@XmlAttribute(name = "skill_lv")
	private String skillLv;
	
	@XmlAttribute(name = "gods_lv")
	private String godsLv;
	
	@XmlAttribute(name = "strengthenlevel")
	private int strengthenlevel;
	

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getLvRange() {
		return lvRange;
	}

	public void setLvRange(String lvRange) {
		this.lvRange = lvRange;
	}

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public String getMonsterLv() {
		return monsterLv;
	}

	public void setMonsterLv(String monsterLv) {
		this.monsterLv = monsterLv;
	}

	public String getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(String monsterId) {
		this.monsterId = monsterId;
	}

	public String getMonsterProp() {
		return monsterProp;
	}

	public void setMonsterProp(String monsterProp) {
		this.monsterProp = monsterProp;
	}

	public String getSkillLv() {
		return skillLv;
	}

	public void setSkillLv(String skillLv) {
		this.skillLv = skillLv;
	}

	public String getGodsLv() {
		return godsLv;
	}

	public void setGodsLv(String godsLv) {
		this.godsLv = godsLv;
	}

	public int getStrengthenlevel() {
		return strengthenlevel;
	}

	public void setStrengthenlevel(int strengthenlevel) {
		this.strengthenlevel = strengthenlevel;
	}
	
	
	
	
	
	
	
	
	

}
