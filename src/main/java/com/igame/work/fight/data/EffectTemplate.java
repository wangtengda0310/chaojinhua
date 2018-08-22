package com.igame.work.fight.data;



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
public class EffectTemplate {
	

	@XmlAttribute(name = "effect")
	private int effect;
	
	
	//生效类型1,怪物2技能
	@XmlAttribute(name = "effect_type")
	private int effectType;
	
	
	//生效条件
	@XmlAttribute(name = "camp_limit")
	private String campLimit;
	
	//提升值
	@XmlAttribute(name = "limit_up")
	private int limitUp;
	
	//效果ID
	@XmlAttribute(name = "effect_id")
	private String effectId;

	//效果值
	@XmlAttribute(name = "effect_value")
	private String effectValue;
	
	//效果提升值
	@XmlAttribute(name = "up_value")
	private String upValue;
	
	//可叠加次数
	@XmlAttribute(name = "repeat")
	private String repeat;
	
	//持续时间	
	@XmlAttribute(name = "times")
	private int times;

	//hot触发时间
	@XmlAttribute(name = "hot_time")
	private int hotTime;
	
	//hot值
	@XmlAttribute(name = "hot_value")
	private float hotValue;
	
	//hotUP值
	@XmlAttribute(name = "up_vlaue")
	private float upVlaue;
	
	//是否固定区域
	@XmlAttribute(name = "follow")
	private int follow;
	
	@XmlAttribute(name = "touch_time")
	private String touchTime;//触发时间

	public int getEffect() {
		return effect;
	}

	public void setEffect(int effect) {
		this.effect = effect;
	}

	public int getEffectType() {
		return effectType;
	}

	public void setEffectType(int effectType) {
		this.effectType = effectType;
	}

	public String getCampLimit() {
		return campLimit;
	}

	public void setCampLimit(String campLimit) {
		this.campLimit = campLimit;
	}

	public int getLimitUp() {
		return limitUp;
	}

	public void setLimitUp(int limitUp) {
		this.limitUp = limitUp;
	}

	public String getEffectId() {
		return effectId;
	}

	public void setEffectId(String effectId) {
		this.effectId = effectId;
	}

	public String getEffectValue() {
		return effectValue;
	}

	public void setEffectValue(String effectValue) {
		this.effectValue = effectValue;
	}

	public String getUpValue() {
		return upValue;
	}

	public void setUpValue(String upValue) {
		this.upValue = upValue;
	}

	public String getRepeat() {
		return repeat;
	}

	public void setRepeat(String repeat) {
		this.repeat = repeat;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public int getHotTime() {
		return hotTime;
	}

	public void setHotTime(int hotTime) {
		this.hotTime = hotTime;
	}

	public float getHotValue() {
		return hotValue;
	}

	public void setHotValue(float hotValue) {
		this.hotValue = hotValue;
	}

	public float getUpVlaue() {
		return upVlaue;
	}

	public void setUpVlaue(float upVlaue) {
		this.upVlaue = upVlaue;
	}

	public int getFollow() {
		return follow;
	}

	public void setFollow(int follow) {
		this.follow = follow;
	}

	public String getTouchTime() {
		return touchTime;
	}

	public void setTouchTime(String touchTime) {
		this.touchTime = touchTime;
	}
	
	
	
	
	
	

}
