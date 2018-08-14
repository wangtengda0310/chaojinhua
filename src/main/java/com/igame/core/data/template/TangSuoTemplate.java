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
public class TangSuoTemplate {
	


	@XmlAttribute(name = "num", required = true)
	private int num;
	
	@XmlAttribute(name = "unlock")
	private int unlock;
	
	@XmlAttribute(name = "site1")
	private String site1;
	
	@XmlAttribute(name = "site2")
	private String site2;
	
	@XmlAttribute(name = "site3")
	private String site3;
	
	@XmlAttribute(name = "site4")
	private String site4;
	
	@XmlAttribute(name = "site5")
	private String site5;
	
	@XmlAttribute(name = "up")
	private double up;
	
	@XmlAttribute(name = "gold")
	private long gold;
	
	@XmlAttribute(name = "item")
	private String item;
	
	@XmlAttribute(name = "hour_rate2")
	private String hour_rate2;
	
	@XmlAttribute(name = "hour_value2")
	private String hour_value2;
	
	@XmlAttribute(name = "hour_rate6")
	private String hour_rate6;
	
	@XmlAttribute(name = "hour_value6")
	private String hour_value6;
	
	@XmlAttribute(name = "hour_rate10")
	private String hour_rate10;
	
	@XmlAttribute(name = "hour_value10")
	private String hour_value10;
	
	@XmlAttribute(name = "lmit")
	private int lmit;
	
	@XmlAttribute(name = "limit_item")
	private String limit_item;
	
	@XmlAttribute(name = "limit_rate2")
	private String limit_rate2;
	
	@XmlAttribute(name = "limit_value2")
	private String limit_value2;
	
	@XmlAttribute(name = "limit_rate6")
	private String limit_rate6;
	
	@XmlAttribute(name = "limit_value6")
	private String limit_value6;
	
	@XmlAttribute(name = "limit_rate10")
	private String limit_rate10;
	
	@XmlAttribute(name = "limit_value10")
	private String limit_value10;
	
	
	
	public String getSite(int index){
		if(index == 1){
			return this.site1;
		}else if(index == 2){
			return this.site2;
		}else if(index == 3){
			return this.site3;
		}else if(index == 4){
			return this.site4;
		}else if(index == 5){
			return this.site5;
		}
		return null;
	}
	
	public String getRate(int hour){
		if(hour == 2){
			return this.hour_rate2;
		}else if(hour == 6){
			return this.hour_rate6;
		}else if(hour == 10){
			return this.hour_rate10;
		}
		return null;
	}
	
	public String getValue(int hour){
		if(hour == 2){
			return this.hour_value2;
		}else if(hour == 6){
			return this.hour_value6;
		}else if(hour == 10){
			return this.hour_value10;
		}
		return null;
	}
	
	public String getLimitRate(int hour){
		if(hour == 2){
			return this.limit_rate2;
		}else if(hour == 6){
			return this.limit_rate6;
		}else if(hour == 10){
			return this.limit_rate10;
		}
		return null;
	}
	
	public String getLimitValue(int hour){
		if(hour == 2){
			return this.limit_value2;
		}else if(hour == 6){
			return this.limit_value6;
		}else if(hour == 10){
			return this.limit_value10;
		}
		return null;
	}
	


	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getUnlock() {
		return unlock;
	}

	public void setUnlock(int unlock) {
		this.unlock = unlock;
	}

	public String getSite1() {
		return site1;
	}

	public void setSite1(String site1) {
		this.site1 = site1;
	}

	public String getSite2() {
		return site2;
	}

	public void setSite2(String site2) {
		this.site2 = site2;
	}

	public String getSite3() {
		return site3;
	}

	public void setSite3(String site3) {
		this.site3 = site3;
	}

	public String getSite4() {
		return site4;
	}

	public void setSite4(String site4) {
		this.site4 = site4;
	}

	public String getSite5() {
		return site5;
	}

	public void setSite5(String site5) {
		this.site5 = site5;
	}


	public double getUp() {
		return up;
	}

	public void setUp(double up) {
		this.up = up;
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

	public String getHour_rate2() {
		return hour_rate2;
	}

	public void setHour_rate2(String hour_rate2) {
		this.hour_rate2 = hour_rate2;
	}

	public String getHour_value2() {
		return hour_value2;
	}

	public void setHour_value2(String hour_value2) {
		this.hour_value2 = hour_value2;
	}

	public String getHour_rate6() {
		return hour_rate6;
	}

	public void setHour_rate6(String hour_rate6) {
		this.hour_rate6 = hour_rate6;
	}

	public String getHour_value6() {
		return hour_value6;
	}

	public void setHour_value6(String hour_value6) {
		this.hour_value6 = hour_value6;
	}

	public String getHour_rate10() {
		return hour_rate10;
	}

	public void setHour_rate10(String hour_rate10) {
		this.hour_rate10 = hour_rate10;
	}

	public String getHour_value10() {
		return hour_value10;
	}

	public void setHour_value10(String hour_value10) {
		this.hour_value10 = hour_value10;
	}

	public int getLmit() {
		return lmit;
	}

	public void setLmit(int lmit) {
		this.lmit = lmit;
	}

	public String getLimit_item() {
		return limit_item;
	}

	public void setLimit_item(String limit_item) {
		this.limit_item = limit_item;
	}

	public String getLimit_rate2() {
		return limit_rate2;
	}

	public void setLimit_rate2(String limit_rate2) {
		this.limit_rate2 = limit_rate2;
	}

	public String getLimit_value2() {
		return limit_value2;
	}

	public void setLimit_value2(String limit_value2) {
		this.limit_value2 = limit_value2;
	}

	public String getLimit_rate6() {
		return limit_rate6;
	}

	public void setLimit_rate6(String limit_rate6) {
		this.limit_rate6 = limit_rate6;
	}

	public String getLimit_value6() {
		return limit_value6;
	}

	public void setLimit_value6(String limit_value6) {
		this.limit_value6 = limit_value6;
	}

	public String getLimit_rate10() {
		return limit_rate10;
	}

	public void setLimit_rate10(String limit_rate10) {
		this.limit_rate10 = limit_rate10;
	}

	public String getLimit_value10() {
		return limit_value10;
	}

	public void setLimit_value10(String limit_value10) {
		this.limit_value10 = limit_value10;
	}

	
	
	
	
	
	

}
