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
public class TongDiaoTemplate {
	

	@XmlAttribute(name = "suit_lv")
	private int suitLv;
	
	@XmlAttribute(name = "suit_item")
	private String suitItem;
	
	@XmlAttribute(name = "effect_id")
	private String effectId;
	
	@XmlAttribute(name = "ability_up")
	private String abilityUp;
	

	public int getSuitLv() {
		return suitLv;
	}

	public void setSuitLv(int suitLv) {
		this.suitLv = suitLv;
	}

	public String getSuitItem() {
		return suitItem;
	}

	public void setSuitItem(String suitItem) {
		this.suitItem = suitItem;
	}

	public String getEffectId() {
		return effectId;
	}

	public void setEffectId(String effectId) {
		this.effectId = effectId;
	}

	public String getAbilityUp() {
		return abilityUp;
	}

	public void setAbilityUp(String abilityUp) {
		this.abilityUp = abilityUp;
	}

	
	
	
	
	
	
	

}
