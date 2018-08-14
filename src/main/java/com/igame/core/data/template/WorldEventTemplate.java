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
public class WorldEventTemplate {
	


	@XmlAttribute(name = "event_type")
	private int event_type;
	
	@XmlAttribute(name = "difficulty")
	private int level;
	
	@XmlAttribute(name = "unlock")
	private int unlock;
	
	@XmlAttribute(name = "gold")
	private String gold;
	
	@XmlAttribute(name = "drop")
	private String drop;
	
	@XmlAttribute(name = "rate")
	private String rate;
	
	@XmlAttribute(name = "physical")
	private int physical;
	
	@XmlAttribute(name = "times")
	private int times;
	
	@XmlAttribute(name = "monster_id")
	private String monsterId;//
	
	@XmlAttribute(name = "level")
	private String mlevel;//
	
	@XmlAttribute(name = "site")
	private String site;//站位

	public int getEvent_type() {
		return event_type;
	}

	public void setEvent_type(int event_type) {
		this.event_type = event_type;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getUnlock() {
		return unlock;
	}

	public void setUnlock(int unlock) {
		this.unlock = unlock;
	}

	public String getGold() {
		return gold;
	}

	public void setGold(String gold) {
		this.gold = gold;
	}

	public String getDrop() {
		return drop;
	}

	public void setDrop(String drop) {
		this.drop = drop;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getPhysical() {
		return physical;
	}

	public void setPhysical(int physical) {
		this.physical = physical;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public String getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(String monsterId) {
		this.monsterId = monsterId;
	}

	public String getMlevel() {
		return mlevel;
	}

	public void setMlevel(String mlevel) {
		this.mlevel = mlevel;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}
	
	
	
	
	
	
	
	

}
