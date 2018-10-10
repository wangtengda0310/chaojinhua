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
public class PVEMonsterTemplate {
	


	@XmlAttribute(name = "monster_id", required = true)
	private int monsterId;
	
	@XmlAttribute(name = "card_id")
	private int cardId;
	
	@XmlAttribute(name = "lv_show")
	private String lvShow;
	
	@XmlAttribute(name = "monster_hp")
	private int monsterHp;

	@XmlAttribute(name = "monster_atk")
	private int monsterAtk;
	
	@XmlAttribute(name = "monster_speed")
	private int monsterSpeed;
	
	@XmlAttribute(name = "monster_ias")
	private float monsterIas;
	
	@XmlAttribute(name = "monster_rng")
	private int monsterRng;
	
	@XmlAttribute(name = "monster_repel")
	private int monsterRepel;
	
	@XmlAttribute(name = "size")
	private double size;
	
	@XmlAttribute(name = "skill_lv")
	private String skillLv;
	
	@XmlAttribute(name = "fate_skill")
	private String fateSkill;

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

	public String getLvShow() {
		return lvShow;
	}

	public void setLvShow(String lvShow) {
		this.lvShow = lvShow;
	}

	public int getMonsterHp() {
		return monsterHp;
	}

	public void setMonsterHp(int monsterHp) {
		this.monsterHp = monsterHp;
	}

	public int getMonsterAtk() {
		return monsterAtk;
	}

	public void setMonsterAtk(int monsterAtk) {
		this.monsterAtk = monsterAtk;
	}

	public int getMonsterSpeed() {
		return monsterSpeed;
	}

	public void setMonsterSpeed(int monsterSpeed) {
		this.monsterSpeed = monsterSpeed;
	}

	public float getMonsterIas() {
		return monsterIas;
	}

	public void setMonsterIas(float monsterIas) {
		this.monsterIas = monsterIas;
	}

	public int getMonsterRng() {
		return monsterRng;
	}

	public void setMonsterRng(int monsterRng) {
		this.monsterRng = monsterRng;
	}

	public int getMonsterRepel() {
		return monsterRepel;
	}

	public void setMonsterRepel(int monsterRepel) {
		this.monsterRepel = monsterRepel;
	}

	public double getSize() {
		return size;
	}

	public void setSize(double size) {
		this.size = size;
	}

	public String getSkillLv() {
		return skillLv;
	}

	public void setSkillLv(String skillLv) {
		this.skillLv = skillLv;
	}

	public String getFateSkill() {
		return fateSkill;
	}

	public void setFateSkill(String fateSkill) {
		this.fateSkill = fateSkill;
	}
}
