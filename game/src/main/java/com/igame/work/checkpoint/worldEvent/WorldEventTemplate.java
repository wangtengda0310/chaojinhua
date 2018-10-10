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
	private int difficulty;

	@XmlAttribute(name = "drop")
	private String drop;

	@XmlAttribute(name = "event_type")
	private int eventType;

	@XmlAttribute(name = "event_name")
	private int eventName;

	@XmlAttribute(name = "gold")
	private String gold;

	@XmlAttribute(name = "monsterset")
	private String monsterset;//

	@XmlAttribute(name = "physical")
	private int physical;

	@XmlAttribute(name = "rate")
	private String rate;

	@XmlAttribute(name = "times")
	private int times;

	@XmlAttribute(name = "unlock")
	private int unlock;

	public int getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}

	public String getDrop() {
		return drop;
	}

	public void setDrop(String drop) {
		this.drop = drop;
	}

	public int getEventType() {
		return eventType;
	}

	public void setEventType(int eventType) {
		this.eventType = eventType;
	}

	public int getEventName() {
		return eventName;
	}

	public void setEventName(int eventName) {
		this.eventName = eventName;
	}

	public String getGold() {
		return gold;
	}

	public void setGold(String gold) {
		this.gold = gold;
	}

	public String getMonsterset() {
		return monsterset;
	}

	public void setMonsterset(String monsterset) {
		this.monsterset = monsterset;
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
