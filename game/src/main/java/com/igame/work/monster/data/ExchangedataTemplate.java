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
public class ExchangedataTemplate {
	

	@XmlAttribute(name = "exchange_type")
	private int exchange_type;
	
	@XmlAttribute(name = "times")
	private int times;
	
	@XmlAttribute(name = "gem")
	private int gem;

	
	@XmlAttribute(name = "exchange_value")
	private String exchange_value;


	public int getExchange_type() {
		return exchange_type;
	}


	public void setExchange_type(int exchange_type) {
		this.exchange_type = exchange_type;
	}


	public int getTimes() {
		return times;
	}


	public void setTimes(int times) {
		this.times = times;
	}


	public int getGem() {
		return gem;
	}


	public void setGem(int gem) {
		this.gem = gem;
	}


	public int getExchange_value() {
		return 100;
	}


	public void setExchange_value(String exchange_value) {
		this.exchange_value = exchange_value;
	}

	
	
	
	
	
	
	
	
	

}
