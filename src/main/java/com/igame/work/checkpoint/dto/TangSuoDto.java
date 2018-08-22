package com.igame.work.checkpoint.dto;

import com.igame.work.user.dto.Player;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.work.checkpoint.data.TangSuoTemplate;
import com.igame.util.MyUtil;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(noClassnameStored = true)
public class TangSuoDto {
	
	private int id;
	
	private String mons = "0,0,-1,-1,-1";//上阵怪物列表
	
	@JsonIgnore
	private long startTime;//开始时间
	
	@Transient
	private long leftTime;
	
	private int state;//0-未在探索 1-探索中

	private int isHelp;	//0=未帮助,1=已帮助	如果已帮助，提前 90 分钟结束

	@JsonIgnore
	private long accPlayerId;	//热心好友ID

	private String accPlayerName = "";	//热心好友昵称
	
	
	public TangSuoDto(){}
	
	public TangSuoDto(TangSuoTemplate ts){
		this.id = ts.getNum();
		String[] ls = mons.split(",");
		if("-1".equals(ts.getSite1())){
			ls[0] = "0";
		}
		if("-1".equals(ts.getSite2())){
			ls[1] = "0";
		}
		if("-1".equals(ts.getSite3())){
			ls[2] = "0";
		}
		if("-1".equals(ts.getSite4())){
			ls[3] = "0";
		}
		if("-1".equals(ts.getSite5())){
			ls[4] = "0";
		}
		this.mons = MyUtil.toString(ls, ",");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMons() {
		return mons;
	}

	public void setMons(String mons) {
		this.mons = mons;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getLeftTime() {
		return leftTime;
	}

	public void setLeftTime(long leftTime) {
		this.leftTime = leftTime;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getIsHelp() {
		return isHelp;
	}

	public void setIsHelp(int isHelp) {
		this.isHelp = isHelp;
	}

	public long getAccPlayerId() {
		return accPlayerId;
	}

	public void setAccPlayerId(long accPlayerId) {
		this.accPlayerId = accPlayerId;
	}

	public String getAccPlayerName() {
		return accPlayerName;
	}

	public void setAccPlayerName(String accPlayerName) {
		this.accPlayerName = accPlayerName;
	}

	public void calLeftTime(long now){
		if(startTime == 0 || state == 0){
			this.leftTime = 0;
		}else{
			if (this.isHelp == 0)
				this.leftTime = (startTime + state * 3600 * 1000) - now > 0 ? ((startTime + state * 3600 * 1000) - now)/1000 : 0;
			else if (this.isHelp == 1)
				this.leftTime = (startTime + state * 3600 * 1000) - now - (5400 * 1000) > 0 ?
						((startTime + state * 3600 * 1000) - now - (5400 * 1000))/1000 : 0;
		}
				
	}

	public void help(Player player) {
		this.isHelp = 1;
		this.accPlayerId = player.getPlayerId();
		this.accPlayerName = player.getNickname();
	}

	public void clearhelp() {
		this.isHelp = 0;
		this.accPlayerId = -1;
		this.accPlayerName = "";
	}
}
