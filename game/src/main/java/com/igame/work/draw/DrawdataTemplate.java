package com.igame.work.draw;



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
public class DrawdataTemplate {
	

	@XmlAttribute(name = "draw_type")
	private int drawType;
	
	@XmlAttribute(name = "draw_lv")
	private int drawLv;
	
	@XmlAttribute(name = "draw_value")
	private int drawValue;
	
	@XmlAttribute(name = "limit")
	private String limit;
	
	@XmlAttribute(name = "item")
	private String item;
	
	@XmlAttribute(name = "gem")
	private int diamond;
	
	@XmlAttribute(name = "reward_id")
	private int rewardId;
	
	@XmlAttribute(name = "draw_exp")
	private int drawExp;

	public int getDrawType() {
		return drawType;
	}

	public void setDrawType(int drawType) {
		this.drawType = drawType;
	}

	public int getDrawLv() {
		return drawLv;
	}

	public void setDrawLv(int drawLv) {
		this.drawLv = drawLv;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}

	public int getRewardId() {
		return rewardId;
	}

	public void setRewardId(int rewardId) {
		this.rewardId = rewardId;
	}

	public int getDrawExp() {
		return drawExp;
	}

	public void setDrawExp(int drawExp) {
		this.drawExp = drawExp;
	}

	public int getDrawValue() {
		return drawValue;
	}

	public void setDrawValue(int drawValue) {
		this.drawValue = drawValue;
	}
	

	
	
	
	
	
	
	
	
	
	

}
