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
public class StrengthenrewardTemplate {
	

	@XmlAttribute(name = "strengthen_type")
	private int strengthen_type;
	
	
	@XmlAttribute(name = "strengthen_exp")
	private String strengthen_exp;
	
	@XmlAttribute(name = "strengthen_reward")
	private String strengthen_reward;
	
	@XmlAttribute(name = "value")
	private String value;
	
	@XmlAttribute(name = "rate")
	private String rate;

	public int getStrengthen_type() {
		return strengthen_type;
	}

	public void setStrengthen_type(int strengthen_type) {
		this.strengthen_type = strengthen_type;
	}

	public String getStrengthen_exp() {
		return strengthen_exp;
	}

	public void setStrengthen_exp(String strengthen_exp) {
		this.strengthen_exp = strengthen_exp;
	}

	public String getStrengthen_reward() {
		return strengthen_reward;
	}

	public void setStrengthen_reward(String strengthen_reward) {
		this.strengthen_reward = strengthen_reward;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	
	
	
	
	
	
	
	

}
