package com.igame.work.item.data;



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
public class PropGroupTemplate {
	


	@XmlAttribute(name = "num", required = true)
	private int num;
	
	@XmlAttribute(name = "item_id")
	private int itemId;
	
	@XmlAttribute(name = "group")
	private int group;
	
	@XmlAttribute(name = "gold")
	private long gold;
	
	@XmlAttribute(name = "rate")
	private int rate;
	
	@XmlAttribute(name = "gem")
	private int gem;
	
	@XmlAttribute(name = "item")
	private int item;
	
	@XmlAttribute(name = "get_item")
	private int getItem;
	


	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	public long getGold() {
		return gold;
	}

	public void setGold(long gold) {
		this.gold = gold;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getGem() {
		return gem;
	}

	public void setGem(int gem) {
		this.gem = gem;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

	public int getGetItem() {
		return getItem;
	}

	public void setGetItem(int getItem) {
		this.getItem = getItem;
	}
	
	
	
	
	
	

}
