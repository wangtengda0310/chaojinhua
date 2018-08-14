package com.igame.work.checkpoint.dto;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.core.db.BasicVO;


@Entity(value = "WordEventDto", noClassnameStored = true)
public class WordEventDto    extends BasicVO  {
	
	@Indexed
	@JsonIgnore
	public long playerId;//所属角色ID
	
	private int eventType;
	
	private String level = "";
	
	private int count;	
	
	
	@Transient
	@JsonIgnore
	public int dtate;//数据库状态 0-NO 1-新增 2-更新 3-删除
	
	
	public WordEventDto(){
		
	}


	public WordEventDto(long playerId, int eventType, String level, int count,
			int dtate) {
		super();
		this.playerId = playerId;
		this.eventType = eventType;
		this.level = level;
		this.count = count;
		this.dtate = dtate;
	}
	

	public long getPlayerId() {
		return playerId;
	}


	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}


	public int getEventType() {
		return eventType;
	}


	public void setEventType(int eventType) {
		this.eventType = eventType;
	}


	public String getLevel() {
		return level;
	}


	public void setLevel(String level) {
		this.level = level;
	}


	public int getCount() {
		return count;
	}


	public void setCount(int count) {
		this.count = count;
	}


	public int getDtate() {
		return dtate;
	}


	public void setDtate(int ndtate) {
		switch (ndtate) {
			case 1://添加 
				if (this.dtate == 2 || this.dtate == 3)
					this.dtate = 2;
				else
					this.dtate = 1;
				break;
			case 2://更新
				if(this.dtate == 1){
					this.dtate = 1;
				} else{
					this.dtate = 2;
				}
				break;
			case 3://删除
				if (this.dtate == 1)
					this.dtate = 0;
				else {
					this.dtate = 3;
				}
				break;
			default:
				this.dtate = ndtate;
				break;
		}
	}

}
