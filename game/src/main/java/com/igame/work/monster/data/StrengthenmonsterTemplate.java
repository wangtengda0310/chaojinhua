package com.igame.work.monster.data;



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
public class StrengthenmonsterTemplate {
	

	@XmlAttribute(name = "num")
	private int num;
	
	@XmlAttribute(name = "monster_id")
	private int monster_id;
	
	@XmlAttribute(name = "monster_lv")
	private int monster_lv;
	
	@XmlAttribute(name = "monster_rarity")
	private int monster_rarity;
	
	@XmlAttribute(name = "meet_rate")
	private int meet_rate;
	
	@XmlAttribute(name = "atk_up")
	private String atk_up;
	
	@XmlAttribute(name = "hp_up")
	private String hp_up;
	
	@XmlAttribute(name = "injured")
	private String injured;
	
	@XmlAttribute(name = "atk_up1")
	private String atk_up1;
	
	@XmlAttribute(name = "hp_up1")
	private String hp_up1;
	
	@XmlAttribute(name = "injured1")
	private String injured1;
	
	@XmlAttribute(name = "repel")
	private String repel;
	
	@XmlAttribute(name = "repeled")
	private String repeled;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getMonster_id() {
		return monster_id;
	}

	public void setMonster_id(int monster_id) {
		this.monster_id = monster_id;
	}

	public int getMonster_lv() {
		return monster_lv;
	}

	public void setMonster_lv(int monster_lv) {
		this.monster_lv = monster_lv;
	}

	public int getMonster_rarity() {
		return monster_rarity;
	}

	public void setMonster_rarity(int monster_rarity) {
		this.monster_rarity = monster_rarity;
	}

	public int getMeet_rate() {
		return meet_rate;
	}

	public void setMeet_rate(int meet_rate) {
		this.meet_rate = meet_rate;
	}

	public String getAtk_up() {
		return atk_up;
	}

	public void setAtk_up(String atk_up) {
		this.atk_up = atk_up;
	}

	public String getHp_up() {
		return hp_up;
	}

	public void setHp_up(String hp_up) {
		this.hp_up = hp_up;
	}

	public String getInjured() {
		return injured;
	}

	public void setInjured(String injured) {
		this.injured = injured;
	}

	public String getAtk_up1() {
		return atk_up1;
	}

	public void setAtk_up1(String atk_up1) {
		this.atk_up1 = atk_up1;
	}

	public String getHp_up1() {
		return hp_up1;
	}

	public void setHp_up1(String hp_up1) {
		this.hp_up1 = hp_up1;
	}

	public String getInjured1() {
		return injured1;
	}

	public void setInjured1(String injured1) {
		this.injured1 = injured1;
	}

	public String getRepel() {
		return repel;
	}

	public void setRepel(String repel) {
		this.repel = repel;
	}

	public String getRepeled() {
		return repeled;
	}

	public void setRepeled(String repeled) {
		this.repeled = repeled;
	}

	
	
	
	

}
