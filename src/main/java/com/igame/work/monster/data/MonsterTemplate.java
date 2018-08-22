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
@XmlRootElement(name = "low")
public class MonsterTemplate {
	


	@XmlAttribute(name = "monster_id", required = true)
	private int monster_id;
	
	@XmlAttribute(name = "atk_type")
	private int atk_type;
	
	@XmlAttribute(name = "monster_camp")
	private String monster_camp;
	
	@XmlAttribute(name = "monster_rarity")
	private int monster_rarity;
	
	@XmlAttribute(name = "monster_hp")
	private int monster_hp;
	
	@XmlAttribute(name = "monster_atk")
	private int monster_atk;
	
	@XmlAttribute(name = "monster_speed")
	private int monster_speed;
	
	@XmlAttribute(name = "monster_ias")
	private float monster_ias;
	
	@XmlAttribute(name = "monster_rng")
	private int monster_rng;
	
	@XmlAttribute(name = "monster_repel")
	private int monster_repel;
	
	@XmlAttribute(name = "hp_up")
	private double hp_up;
	
	@XmlAttribute(name = "atk_up")
	private double atk_up;
	
	@XmlAttribute(name = "skill")
	private String skill;
	
	@XmlAttribute(name = "monstertype")
	private String monstertype;//1近战/2远程/3坦克/4输出/5辅助
	
	@XmlAttribute(name = "bullet_speed")
	private int bulletSpeed;


	public int getMonster_id() {
		return monster_id;
	}

	public void setMonster_id(int monster_id) {
		this.monster_id = monster_id;
	}

	public int getAtk_type() {
		return atk_type;
	}

	public void setAtk_type(int atk_type) {
		this.atk_type = atk_type;
	}

	public String getMonster_camp() {
		return monster_camp;
	}

	public void setMonster_camp(String monster_camp) {
		this.monster_camp = monster_camp;
	}

	public int getMonster_rarity() {
		return monster_rarity;
	}

	public void setMonster_rarity(int monster_rarity) {
		this.monster_rarity = monster_rarity;
	}

	public int getMonster_hp() {
		return monster_hp;
	}

	public void setMonster_hp(int monster_hp) {
		this.monster_hp = monster_hp;
	}

	public int getMonster_atk() {
		return monster_atk;
	}

	public void setMonster_atk(int monster_atk) {
		this.monster_atk = monster_atk;
	}

	public int getMonster_speed() {
		return monster_speed;
	}

	public void setMonster_speed(int monster_speed) {
		this.monster_speed = monster_speed;
	}

	public float getMonster_ias() {
		return monster_ias;
	}

	public void setMonster_ias(float monster_ias) {
		this.monster_ias = monster_ias;
	}

	public int getMonster_rng() {
		return monster_rng;
	}

	public void setMonster_rng(int monster_rng) {
		this.monster_rng = monster_rng;
	}

	public int getMonster_repel() {
		return monster_repel;
	}

	public void setMonster_repel(int monster_repel) {
		this.monster_repel = monster_repel;
	}

	public double getHp_up() {
		return hp_up;
	}

	public void setHp_up(double hp_up) {
		this.hp_up = hp_up;
	}

	public double getAtk_up() {
		return atk_up;
	}

	public void setAtk_up(double atk_up) {
		this.atk_up = atk_up;
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String getMonstertype() {
		return monstertype;
	}

	public void setMonstertype(String monstertype) {
		this.monstertype = monstertype;
	}

	public int getBulletSpeed() {
		return bulletSpeed;
	}

	public void setBulletSpeed(int bulletSpeed) {
		this.bulletSpeed = bulletSpeed;
	}
	
	
	
	

}
