package com.igame.work.monster.dto;



import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.core.db.BasicDto;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(value = "Gods", noClassnameStored = true)
public class Gods   extends BasicDto implements Cloneable {
	
	
	@Indexed
	@JsonIgnore
	public long playerId;//角色ID
	
	public int godsType;
	
	public int godsLevel;

	
	@Transient
	@JsonIgnore
	public int dtate;//数据库状态 0-NO 1-新增 2-更新 3-删除

	
	public Gods(){
		
	}
	
	

	
	public Gods(long playerId, int godsType, int godsLevel, int dtate) {
		super();
		this.playerId = playerId;
		this.godsType = godsType;
		this.godsLevel = godsLevel;
		this.dtate = dtate;
	}




	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	
	


	public int getGodsType() {
		return godsType;
	}


	public void setGodsType(int godsType) {
		this.godsType = godsType;
	}


	public int getGodsLevel() {
		return godsLevel;
	}


	public void setGodsLevel(int godsLevel) {
		this.godsLevel = godsLevel;
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




	public Gods clonew(){
		try {
			return (Gods)super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	

	
	

	
	
	
	
	
	

}
