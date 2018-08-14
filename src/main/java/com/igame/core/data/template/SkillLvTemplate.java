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
public class SkillLvTemplate {
	
	@XmlAttribute(name = "skill_id", required = true)
	private int skillId;

	@XmlAttribute(name = "use_item")
	private String useItem;

	@XmlAttribute(name = "skill_exp")
	private int skillExp;

	@XmlAttribute(name = "get_exp")
	private String getExp;

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	public String getUseItem() {
		return useItem;
	}

	public void setUseItem(String useItem) {
		this.useItem = useItem;
	}

	public int getSkillExp() {
		return skillExp;
	}

	public void setSkillExp(int skillExp) {
		this.skillExp = skillExp;
	}

	public String getGetExp() {
		return getExp;
	}

	public void setGetExp(String getExp) {
		this.getExp = getExp;
	}
}
