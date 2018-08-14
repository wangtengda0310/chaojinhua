package com.igame.work.user.dto;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.util.MyUtil;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(noClassnameStored = true)
public class TongHuaDto {
	
	private int id;
	
	private int sid;	
	
	/**
	 *"type,state,rewardtype,rewardId,count;";//如1,0,1,1,1;2,-1,3,200005,100
	 * type:0-普通 1-怪物 2-强化袍子 3-进化袍子 4-神性袍子
     * state:-1-未解锁 0-已解锁/可开启 1-倒计时/进行中 2-可领取 3-已领取/已完结	
	 * rewardtype:1-怪物关卡  3-道具 5-同化经验值
	 * rewardId:当rewardtype为1 对应strengthenmonster.xml中的num 
	 *  当rewardtype为3 对应道具ID
	 *  当rewardtype为5 为5
	 * count:对应的数量
	 */
	private String tongStr = "";	
	
	@JsonIgnore
	private long startTime;
	
	private int timeIndex;//唯一一个倒计时的位置 1-22
	
	@Transient
	private long leftTime;
	
	@JsonIgnore
	private int timeCount;
	
	
	
	@JsonIgnore
	private long startRefTime;//刷新时间
	
	@Transient
	private long leftRefTime;//
	
	private String name = "";
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public String getTongStr() {
		return tongStr;
	}

	public void setTongStr(String tongStr) {
		this.tongStr = tongStr;
	}

	public long getLeftTime() {
		return leftTime;
	}

	public void setLeftTime(long leftTime) {
		this.leftTime = leftTime;
	}

	public int getTimeCount() {
		return timeCount;
	}

	public void setTimeCount(int timeCount) {
		this.timeCount = timeCount;
	}
	
	public long calLeftTime(){
		if(startTime >0 && timeCount > 0){
			leftTime = (startTime + timeCount * 60000 -System.currentTimeMillis())/1000;
			if(leftTime < 0){
				leftTime = 0;
			}
			if(timeIndex > 0){
				if(leftTime <= 0){//时间到
					String[] tss = this.getTongStr().split(";");
					String[] t = tss[timeIndex-1].split(",");//"type,state,rewardtype,rewardId,count"
					t[1] = "2";
					tss[timeIndex-1] = MyUtil.toString(t, ",");
					this.setTongStr(MyUtil.toString(tss, ";"));
				}
			}
		}else{
			leftTime = 0;
		}
		return leftTime;
	}

	public int getTimeIndex() {
		return timeIndex;
	}

	public void setTimeIndex(int timeIndex) {
		this.timeIndex = timeIndex;
	}

	public long getStartRefTime() {
		return startRefTime;
	}

	public void setStartRefTime(long startRefTime) {
		this.startRefTime = startRefTime;
	}

	public long getLeftRefTime() {
		return leftRefTime;
	}

	public void setLeftRefTime(long leftRefTime) {
		this.leftRefTime = leftRefTime;
	}
	
	
	public long calRefLeftTime(){
		if(startRefTime >0){
			leftRefTime = (startRefTime + 360 * 60000 -System.currentTimeMillis())/1000;
			if(leftRefTime < 0){
				leftRefTime = 0;
			}
		}else{
			leftRefTime = 0;
		}
		return leftRefTime;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
	

}
