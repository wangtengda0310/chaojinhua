package com.igame.work.monster.dto;

import org.mongodb.morphia.annotations.Entity;


/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(noClassnameStored = true)
public class WuEffect {
	
	private int   effectId;//效果ID
	
	private float value;//数值
	
	public WuEffect(){}
	
	public WuEffect(int effectId) {
		super();
		this.effectId = effectId;
		if(effectId == 101){
			this.value = 5;
		}else if(effectId == 105){
			this.value = 10;
		}else if(effectId == 127){
			this.value = 30;
		}else if(effectId == 107){
			this.value = 30;
		}
	}
	

	public WuEffect(int effectId, float value) {
		super();
		this.effectId = effectId;
		this.value = value;
	}



	public int getEffectId() {
		return effectId;
	}

	public void setEffectId(int effectId) {
		this.effectId = effectId;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	
}
