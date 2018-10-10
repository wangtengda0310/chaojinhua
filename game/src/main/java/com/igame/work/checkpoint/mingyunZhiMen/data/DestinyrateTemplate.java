package com.igame.work.checkpoint.mingyunZhiMen.data;



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
public class DestinyrateTemplate {
	

	@XmlAttribute(name = "destiny_type")
	private int destinyType;
	
	@XmlAttribute(name = "rate")
	private  int rate;
	
	@XmlAttribute(name = "up_rate")
	private  int upRate;
	
	@XmlAttribute(name = "max_times")
	private  int maxTimes;
	
	@XmlAttribute(name = "door_type")
	private String doorType;
	
	@XmlAttribute(name = "random_rate")
	private  String randomRate;

	public int getDestinyType() {
		return destinyType;
	}

	public void setDestinyType(int destinyType) {
		this.destinyType = destinyType;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getUpRate() {
		return upRate;
	}

	public void setUpRate(int upRate) {
		this.upRate = upRate;
	}

	public int getMaxTimes() {
		return maxTimes;
	}

	public void setMaxTimes(int maxTimes) {
		this.maxTimes = maxTimes;
	}

	public String getDoorType() {
		return doorType;
	}

	public void setDoorType(String doorType) {
		this.doorType = doorType;
	}

	public String getRandomRate() {
		return randomRate;
	}

	public void setRandomRate(String randomRate) {
		this.randomRate = randomRate;
	}
	

	
	
	
	
	
	
	
	
	
	
	
	

}
