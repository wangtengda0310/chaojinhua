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
public class SkillTemplate {
	
	//技能Id
	@XmlAttribute(name = "skill_id")
	private int skillId;
	
	
	// 技能类型
	//	1伤害型
	//	2buff型
	@XmlAttribute(name = "skill_type")
	private int skillType;
	
	//是否范围技能
	//1范围
	//2非范围
	@XmlAttribute(name = "range_type")
	private int rangeType;
	
	//子类型
	//1直接触发
	//2连锁触发
	//3怪物技能
	@XmlAttribute(name = "subtype")
	private int subtype;
	
	//技能等级改变类型
	//1生效条件
	//2伤害条件
	//3效果条件
	@XmlAttribute(name = "skill_lv")
	private int skillLv;
	

	//技能免疫
	//1.无
	//2. 免疫沉默
	//3. 免疫清除
	@XmlAttribute(name = "skill_priority")
	private int skillPriority;
	
	//触发类型
	
	@XmlAttribute(name = "touch")
	private int touch;
	
	//触发值
	@XmlAttribute(name = "touch_value")
	private String touchValue;
	
	//提升值
	@XmlAttribute(name = "touch_up")
	private float touchUp;
	
	//生效阵营0友1敌2全部
	@XmlAttribute(name = "camp")
	private String camp;
	
	//生效目标
	@XmlAttribute(name = "camp_target")
	private String campTarget;
	
	//中心点1命中目标2自身
	@XmlAttribute(name = "centre")
	private int centre;
	
	//生效范围
	@XmlAttribute(name = "range")
	private int range;
	
	
	//伤害百分比
	@XmlAttribute(name = "hurt")
	private int hurt;
	
	//提升值
	@XmlAttribute(name = "hurt_up")
	private float hurtUp;
	
	//对应的效果关联
	@XmlAttribute(name = "effect")
	private String effect;
	
	//对应的技能关联
	@XmlAttribute(name = "effect_touch")
	private String effectTouch;
	

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public int getSkillType() {
		return skillType;
	}

	public void setSkillType(int skillType) {
		this.skillType = skillType;
	}

	public int getRangeType() {
		return rangeType;
	}

	public void setRangeType(int rangeType) {
		this.rangeType = rangeType;
	}

	public int getSubtype() {
		return subtype;
	}

	public void setSubtype(int subtype) {
		this.subtype = subtype;
	}

	public int getSkillLv() {
		return skillLv;
	}

	public void setSkillLv(int skillLv) {
		this.skillLv = skillLv;
	}

	public int getSkillPriority() {
		return skillPriority;
	}

	public void setSkillPriority(int skillPriority) {
		this.skillPriority = skillPriority;
	}

	public int getTouch() {
		return touch;
	}

	public void setTouch(int touch) {
		this.touch = touch;
	}

	public String getTouchValue() {
		return touchValue;
	}

	public void setTouchValue(String touchValue) {
		this.touchValue = touchValue;
	}

	public String getCamp() {
		return camp;
	}

	public void setCamp(String camp) {
		this.camp = camp;
	}

	public String getCampTarget() {
		return campTarget;
	}

	public void setCampTarget(String campTarget) {
		this.campTarget = campTarget;
	}

	public int getCentre() {
		return centre;
	}

	public void setCentre(int centre) {
		this.centre = centre;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getHurt() {
		return hurt;
	}

	public void setHurt(int hurt) {
		this.hurt = hurt;
	}

	public float getHurtUp() {
		return hurtUp;
	}

	public void setHurtUp(float hurtUp) {
		this.hurtUp = hurtUp;
	}

	public String getEffect() {
		return effect;
	}

	public void setEffect(String effect) {
		this.effect = effect;
	}

	public float getTouchUp() {
		return touchUp;
	}

	public void setTouchUp(float touchUp) {
		this.touchUp = touchUp;
	}

	public String getEffectTouch() {
		return effectTouch;
	}

	public void setEffectTouch(String effectTouch) {
		this.effectTouch = effectTouch;
	}

	
	
	
	
	

}
