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
public class GodsdataTemplate {
	

	@XmlAttribute(name = "gods_type")
	private int godsType;
	
	@XmlAttribute(name = "unlock_lv")
	private int unlockLv;
	
	@XmlAttribute(name = "gods_level")
	private int godsLevel;
	
	@XmlAttribute(name = "gold")
	private long gold;
	
	@XmlAttribute(name = "item")
	private String item;
	
	@XmlAttribute(name = "gods_effect")
	private int godsEffect;

	public int getGodsType() {
		return godsType;
	}

	public void setGodsType(int godsType) {
		this.godsType = godsType;
	}

	public int getUnlockLv() {
		return unlockLv;
	}

	public void setUnlockLv(int unlockLv) {
		this.unlockLv = unlockLv;
	}

	public int getGodsLevel() {
		return godsLevel;
	}

	public void setGodsLevel(int godsLevel) {
		this.godsLevel = godsLevel;
	}

	public long getGold() {
		return gold;
	}

	public void setGold(long gold) {
		this.gold = gold;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getGodsEffect() {
		return godsEffect;
	}

	public void setGodsEffect(int godsEffect) {
		this.godsEffect = godsEffect;
	}
	
	
	
	
	
	
	
	
	
	
	

}
