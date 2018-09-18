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
public class StrengthenplaceTemplate {
	

	@XmlAttribute(name = "num")
	private int num;
	
	@XmlAttribute(name = "strengthen_place")
	private String name;
	
	@XmlAttribute(name = "strengthen_type")
	private int strengthen_type;
	
	@XmlAttribute(name = "total")
	private int total;
	
	@XmlAttribute(name = "value")
	private String value;
	
	@XmlAttribute(name = "rate")
	private String rate;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getStrengthen_type() {
		return strengthen_type;
	}

	public void setStrengthen_type(int strengthen_type) {
		this.strengthen_type = strengthen_type;
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

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

	
	
	
	
	
	

}
