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
public class StrengthenlevelTemplate {
	


	@XmlAttribute(name = "strengthen_lv")
	private int strengthen_lv;
	
	@XmlAttribute(name = "exp")
	private int exp;
	
	@XmlAttribute(name = "atk_limit")
	private int atk_limit;
	
	@XmlAttribute(name = "hp_limit")
	private int hp_limit;
	
	@XmlAttribute(name = "injured_limit")
	private int injured_limit;
	
	@XmlAttribute(name = "atk_limit1")
	private float atk_limit1;
	
	
	@XmlAttribute(name = "hp_limit1")
	private float hp_limit1;
	
	
	@XmlAttribute(name = "injured_limit1")
	private float injured_limit1;
	
	
	@XmlAttribute(name = "repel_limit")
	private float repel_limit;
	
	
	@XmlAttribute(name = "repeled_limit")
	private float repeled_limit;


	public int getStrengthen_lv() {
		return strengthen_lv;
	}


	public void setStrengthen_lv(int strengthen_lv) {
		this.strengthen_lv = strengthen_lv;
	}


	public int getExp() {
		return exp;
	}


	public void setExp(int exp) {
		this.exp = exp;
	}


	public int getAtk_limit() {
		return atk_limit;
	}


	public void setAtk_limit(int atk_limit) {
		this.atk_limit = atk_limit;
	}


	public int getHp_limit() {
		return hp_limit;
	}


	public void setHp_limit(int hp_limit) {
		this.hp_limit = hp_limit;
	}


	public int getInjured_limit() {
		return injured_limit;
	}


	public void setInjured_limit(int injured_limit) {
		this.injured_limit = injured_limit;
	}


	public float getAtk_limit1() {
		return atk_limit1;
	}


	public void setAtk_limit1(float atk_limit1) {
		this.atk_limit1 = atk_limit1;
	}


	public float getHp_limit1() {
		return hp_limit1;
	}


	public void setHp_limit1(float hp_limit1) {
		this.hp_limit1 = hp_limit1;
	}


	public float getInjured_limit1() {
		return injured_limit1;
	}


	public void setInjured_limit1(float injured_limit1) {
		this.injured_limit1 = injured_limit1;
	}


	public float getRepel_limit() {
		return repel_limit;
	}


	public void setRepel_limit(float repel_limit) {
		this.repel_limit = repel_limit;
	}


	public float getRepeled_limit() {
		return repeled_limit;
	}


	public void setRepeled_limit(float repeled_limit) {
		this.repeled_limit = repeled_limit;
	}
	
	
	
	
	

}
