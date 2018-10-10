package com.igame.work.checkpoint.guanqia.data;



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
public class DropDataTemplate {
	


	@XmlAttribute(name = "drop_id", required = true)
	private int dropId;
	
	@XmlAttribute(name = "first_drop")
	private String firstDrop;
	
	@XmlAttribute(name = "item_drop")
	private String itemDrop;//
	
	@XmlAttribute(name = "gold_drop")
	private long goldDrop;

	@XmlAttribute(name = "rate")
	private String rate;

	public int getDropId() {
		return dropId;
	}

	public void setDropId(int dropId) {
		this.dropId = dropId;
	}

	public String getFirstDrop() {
		return firstDrop;
	}

	public void setFirstDrop(String firstDrop) {
		this.firstDrop = firstDrop;
	}

	public String getItemDrop() {
		return itemDrop;
	}

	public void setItemDrop(String itemDrop) {
		this.itemDrop = itemDrop;
	}

	public long getGoldDrop() {
		return goldDrop;
	}

	public void setGoldDrop(long goldDrop) {
		this.goldDrop = goldDrop;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}


	
	
	
	
	

}
