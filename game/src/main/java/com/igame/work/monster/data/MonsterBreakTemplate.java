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
public class MonsterBreakTemplate {
	


	@XmlAttribute(name = "breaklv", required = true)
	private int breaklv;

	
	@XmlAttribute(name = "gold")
	private long gold;
	
	@XmlAttribute(name = "item")
	private String item;
	
	@XmlAttribute(name = "change_gold")
	private long change_gold;
	
	@XmlAttribute(name = "change_gem")
	private int diamond;

	public int getBreaklv() {
		return breaklv;
	}

	public void setBreaklv(int breaklv) {
		this.breaklv = breaklv;
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

	public long getChange_gold() {
		return change_gold;
	}

	public void setChange_gold(long change_gold) {
		this.change_gold = change_gold;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}


	

	
	
	
	

}
