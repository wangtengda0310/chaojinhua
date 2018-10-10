package com.igame.work.activitylimit;



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
public class ShopActivityDataTemplate {


	@XmlAttribute(name = "num")
	private int num;

	//1日常2成就3限时
	@XmlAttribute(name = "giftbag_name")
	private  String giftbag_name;

	@XmlAttribute(name = "text")
	private  String text;

	//任务限制(1玩家等级；2任务完成)
	@XmlAttribute(name = "touch_limit")
	private int touch_limit;

	//任务限制(1玩家等级；2任务完成)
	@XmlAttribute(name = "item_id")
	private int item_id;

	//任务要求
	@XmlAttribute(name = "touch_value")
	private  int touch_value;

	//完成数
	@XmlAttribute(name = "time_range")
	private  int time_range;

	//任务奖励
	@XmlAttribute(name = "time_limit")
	private int time_limit;

	//刷新消耗
	@XmlAttribute(name = "drop_value")
	private  String drop_value;

	//刷新消耗
	@XmlAttribute(name = "cd")
	private  int cd;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getGiftbag_name() {
		return giftbag_name;
	}

	public void setGiftbag_name(String giftbag_name) {
		this.giftbag_name = giftbag_name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getTouch_limit() {
		return touch_limit;
	}

	public void setTouch_limit(int touch_limit) {
		this.touch_limit = touch_limit;
	}

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public int getTouch_value() {
		return touch_value;
	}

	public void setTouch_value(int touch_value) {
		this.touch_value = touch_value;
	}

	public int getTime_range() {
		return time_range;
	}

	public void setTime_range(int time_range) {
		this.time_range = time_range;
	}

	public int getTime_limit() {
		return time_limit;
	}

	public void setTime_limit(int time_limit) {
		this.time_limit = time_limit;
	}

	public String getDrop_value() {
		return drop_value;
	}

	public void setDrop_value(String drop_value) {
		this.drop_value = drop_value;
	}

	public int getCd() {
		return cd;
	}

	public void setCd(int cd) {
		this.cd = cd;
	}
}
