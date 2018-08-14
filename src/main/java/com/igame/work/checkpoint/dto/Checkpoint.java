package com.igame.work.checkpoint.dto;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.core.db.BasicVO;

/**
 * 
 * @author Marcus.Z
 *
 */
@Entity(value = "Checkpoint", noClassnameStored = true)
public class Checkpoint   extends BasicVO {
	
	@Indexed
	@JsonIgnore
	public long playerId;//所属角色ID
	
	private int chapterId;
	
//	private int state;//0-未通过 1-已通过
	
	@Transient
	@JsonIgnore
	public int dtate;//数据库状态 0-NO 1-新增 2-更新 3-删除
	
	
	public Checkpoint(){
		
	}

	
	public Checkpoint(long playerId, int chapterId,int dtate) {
		super();
		this.playerId = playerId;
		this.chapterId = chapterId;
		this.dtate = dtate;
	}




	public long getPlayerId() {
		return playerId;
	}



	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}


	public int getChapterId() {
		return chapterId;
	}




	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
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
