package com.igame.work.user.dto;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.util.MyUtil;


/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(noClassnameStored = true)
public class TongAddDto {
	
	private int tongLevel = 1;//当前同化等级
	
	private int tongExp;//当前同化经验值
	
	private int tongBuyCount;//今日已购买同化币次数			
		
	
	public int attackAdd; //攻击加成值
	
	public int hpAdd;	//HP加成值

	public int damgeRed;//伤害减少值
	
	
	public float attackAddPer; 
	
	public float hpAddPer;	

	public float damgeRedPer;	
	

	public float repelAddPer;
	
	public float repeledAddPer;
	



	public int getHpAdd() {
		return hpAdd;
	}


	public void setHpAdd(int hpAdd) {
		this.hpAdd = hpAdd;
	}


	public int getAttackAdd() {
		return attackAdd;
	}


	public void setAttackAdd(int attackAdd) {
		this.attackAdd = attackAdd;
	}


	public float getRepeledAddPer() {
		return repeledAddPer;
	}


	public void setRepeledAddPer(float repeledAddPer) {
		this.repeledAddPer = repeledAddPer;
	}


	public int getTongLevel() {
		return tongLevel;
	}


	public void setTongLevel(int tongLevel) {
		this.tongLevel = tongLevel;
	}


	public int getTongExp() {
		return tongExp;
	}


	public void setTongExp(int tongExp) {
		this.tongExp = tongExp;
	}


	public int getTongBuyCount() {
		return tongBuyCount;
	}


	public void setTongBuyCount(int tongBuyCount) {
		this.tongBuyCount = tongBuyCount;
	}




	public int getDamgeRed() {
		return damgeRed;
	}


	public void setDamgeRed(int damgeRed) {
		this.damgeRed = damgeRed;
	}


	public float getAttackAddPer() {
		return attackAddPer;
	}


	public void setAttackAddPer(float attackAddPer) {
		this.attackAddPer = attackAddPer;
	}


	public float getHpAddPer() {
		return hpAddPer;
	}


	public void setHpAddPer(float hpAddPer) {
		this.hpAddPer = hpAddPer;
	}


	public float getDamgeRedPer() {
		return damgeRedPer;
	}


	public void setDamgeRedPer(float damgeRedPer) {
		this.damgeRedPer = damgeRedPer;
	}


	public float getRepelAddPer() {
		return repelAddPer;
	}


	public void setRepelAddPer(float repelAddPer) {
		this.repelAddPer = repelAddPer;
	}

	
	
	
	
	
	

}
