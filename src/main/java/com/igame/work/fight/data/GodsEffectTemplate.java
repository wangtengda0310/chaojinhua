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
public class GodsEffectTemplate {
	

	@XmlAttribute(name = "gods_effect")
	private int godsEffect;
	
	
	@XmlAttribute(name = "active_target")
	private int activeTarget;
	
	@XmlAttribute(name = "active_effect")
	private String activeEffect;
	
	@XmlAttribute(name = "active_value")
	private String activeValue;
	
	@XmlAttribute(name = "active_time")
	private float activeTime;
	
	//0友军1敌军2敌军血量最少
	@XmlAttribute(name = "passive_target")
	private int passiveTarget;
	
	//被动效果EFFECT ID
	@XmlAttribute(name = "passive_effect")
	private String passiveEffect;
	
	//被动技能的效果值
	@XmlAttribute(name = "passive_value")
	private String passiveValue;
	
	@XmlAttribute(name = "passive_hot")
	private int passiveHot;
	
	@XmlAttribute(name = "special_effect")
	private String specialEffect;
	
	@XmlAttribute(name = "special_value")
	private String specialValue;

	public int getGodsEffect() {
		return godsEffect;
	}

	public void setGodsEffect(int godsEffect) {
		this.godsEffect = godsEffect;
	}

	public int getActiveTarget() {
		return activeTarget;
	}

	public void setActiveTarget(int activeTarget) {
		this.activeTarget = activeTarget;
	}

	public String getActiveEffect() {
		return activeEffect;
	}

	public void setActiveEffect(String activeEffect) {
		this.activeEffect = activeEffect;
	}

	public String getActiveValue() {
		return activeValue;
	}

	public void setActiveValue(String activeValue) {
		this.activeValue = activeValue;
	}

	public float getActiveTime() {
		return activeTime;
	}

	public void setActiveTime(float activeTime) {
		this.activeTime = activeTime;
	}

	public int getPassiveTarget() {
		return passiveTarget;
	}

	public void setPassiveTarget(int passiveTarget) {
		this.passiveTarget = passiveTarget;
	}

	public String getPassiveEffect() {
		return passiveEffect;
	}

	public void setPassiveEffect(String passiveEffect) {
		this.passiveEffect = passiveEffect;
	}

	public String getPassiveValue() {
		return passiveValue;
	}

	public void setPassiveValue(String passiveValue) {
		this.passiveValue = passiveValue;
	}

	public int getPassiveHot() {
		return passiveHot;
	}

	public void setPassiveHot(int passiveHot) {
		this.passiveHot = passiveHot;
	}

	public String getSpecialEffect() {
		return specialEffect;
	}

	public void setSpecialEffect(String specialEffect) {
		this.specialEffect = specialEffect;
	}

	public String getSpecialValue() {
		return specialValue;
	}

	public void setSpecialValue(String specialValue) {
		this.specialValue = specialValue;
	}
	
	
	
	
	
	

}
