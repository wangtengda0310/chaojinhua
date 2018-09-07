package com.igame.work.activity;


import com.igame.util.DateUtil;
import com.igame.work.user.dto.Player;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;


/**
 * 
 * @author Marcus.Z
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "low")
public class ActivityConfigTemplate {
	

	@XmlAttribute(name = "activity_sign")
	private int activity_sign;
		
	@XmlAttribute(name = "activity_type")
	private int activity_type;
	
	@XmlAttribute(name = "order")
	private int order;

	@XmlAttribute(name = "activity_show")
	private String activity_show;

	@XmlAttribute(name = "activity_name")
	private  String activity_name;
	
	@XmlAttribute(name = "activity_text")
	private  String activity_text;
	
	@XmlAttribute(name = "gift_bag")
	private  int gift_bag;

	@XmlAttribute(name = "start_time")
	private  String start_time;

	@XmlAttribute(name = "time_limit")
	private  int time_limit;

	@XmlAttribute(name = "get_limit")
	private  String get_limit;

	@XmlAttribute(name = "get_value")
	private  String get_value;

	@XmlAttribute(name = "activity_drop")
	private  String activity_drop;

	public int getActivity_sign() {
		return activity_sign;
	}

	public void setActivity_sign(int activity_sign) {
		this.activity_sign = activity_sign;
	}

	public int getActivity_type() {
		return activity_type;
	}

	public void setActivity_type(int activity_type) {
		this.activity_type = activity_type;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getActivity_show() {
		return activity_show;
	}

	public void setActivity_show(String activity_show) {
		this.activity_show = activity_show;
	}

	public String getActivity_name() {
		return activity_name;
	}

	public void setActivity_name(String activity_name) {
		this.activity_name = activity_name;
	}

	public String getActivity_text() {
		return activity_text;
	}

	public void setActivity_text(String activity_text) {
		this.activity_text = activity_text;
	}

	public int getGift_bag() {
		return gift_bag;
	}

	public void setGift_bag(int gift_bag) {
		this.gift_bag = gift_bag;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public int getTime_limit() {
		return time_limit;
	}

	public void setTime_limit(int time_limit) {
		this.time_limit = time_limit;
	}

	public String getGet_limit() {
		return get_limit;
	}

	public void setGet_limit(String get_limit) {
		this.get_limit = get_limit;
	}

	public String getGet_value() {
		return get_value;
	}

	public void setGet_value(String get_value) {
		this.get_value = get_value;
	}

	public String getActivity_drop() {
		return activity_drop;
	}

	public void setActivity_drop(String activity_drop) {
		this.activity_drop = activity_drop;
	}

	public Date startTime(Player player) {
		if ("1".equals(getStart_time())) {
			return player.getBuildTime();
		}
		if (getStart_time().startsWith("2,")) {
			return new Date(Long.parseLong(getStart_time().substring(2)));
		}
		return null;
	}

	public boolean isActive(Player player, Date now) {
		Date startTime = startTime(player);
		return startTime.before(now) && (-1 == getTime_limit() || DateUtil.getIntervalHours(startTime, now)<getTime_limit());
	}
}
