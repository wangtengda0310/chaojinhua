package com.igame.work.checkpoint.worldEvent;



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
public class WorldEventTemplate {
	


	@XmlAttribute(name = "difficulty")
	private int level;

	@XmlAttribute(name = "drop")
	private String drop;

	@XmlAttribute(name = "event_type")
	private int event_type;

	@XmlAttribute(name = "event_name")
	private int event_name;

	@XmlAttribute(name = "gold")
	private String gold;

	@XmlAttribute(name = "monsterset")
	private String monsterId;//

	@XmlAttribute(name = "physical")
	private int physical;

	@XmlAttribute(name = "rate")
	private String rate;

	@XmlAttribute(name = "times")
	private int times;

	@XmlAttribute(name = "unlock")
	private int unlock;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getDrop() {
		return drop;
	}

	public void setDrop(String drop) {
		this.drop = drop;
	}

	public int getEvent_type() {
		return event_type;
	}

	public void setEvent_type(int event_type) {
		this.event_type = event_type;
	}

	public int getEvent_name() {
		return event_name;
	}

	public void setEvent_name(int event_name) {
		this.event_name = event_name;
	}

	public String getGold() {
		return gold;
	}

	public void setGold(String gold) {
		this.gold = gold;
	}

	public String getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(String monsterId) {
		this.monsterId = monsterId;
	}

	public int getPhysical() {
		return physical;
	}

	public void setPhysical(int physical) {
		this.physical = physical;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public int getTimes() {
		return times;
	}

	public void setTimes(int times) {
		this.times = times;
	}

	public int getUnlock() {
		return unlock;
	}

	public void setUnlock(int unlock) {
		this.unlock = unlock;
	}
}
