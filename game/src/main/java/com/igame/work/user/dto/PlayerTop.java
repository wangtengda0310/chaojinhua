package com.igame.work.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.Map;


/**
 * 
 * @author xym
 *
 * 	角色挑战次数上限
 *
 */
@Entity(noClassnameStored = true)
public class PlayerTop {

	@JsonIgnore
	public Map<Integer,Integer> bossCheckPoint = Maps.newHashMap();	//boss关卡挑战上限

	@Transient
	private String bossCheckPointStr = "";	//boss关卡挑战上限

	@JsonIgnore
	public Map<Integer,Integer> generalCheckPoint = Maps.newHashMap();	//一般关卡挑战上限

	@Transient
	@JsonIgnore
	private String generalCheckPointStr = "";	//一般关卡挑战上限

	public int friendPhy;	//好友体力领取上限

	public int friendExplore;	//好友探索加速上限


	public Map<Integer, Integer> getBossCheckPoint() {
		return bossCheckPoint;
	}

	public void setBossCheckPoint(Map<Integer, Integer> bossCheckPoint) {
		this.bossCheckPoint = bossCheckPoint;
	}

	public Map<Integer, Integer> getGeneralCheckPoint() {
		return generalCheckPoint;
	}

	public void setGeneralCheckPoint(Map<Integer, Integer> generalCheckPoint) {
		this.generalCheckPoint = generalCheckPoint;
	}

	public String getBossCheckPointStr() {
		return bossCheckPointStr;
	}

	public void setBossCheckPointStr(String bossCheckPointStr) {
		this.bossCheckPointStr = bossCheckPointStr;
	}

	public String getGeneralCheckPointStr() {
		return generalCheckPointStr;
	}

	public void setGeneralCheckPointStr(String generalCheckPointStr) {
		this.generalCheckPointStr = generalCheckPointStr;
	}

	public int getFriendPhy() {
		return friendPhy;
	}

	public void setFriendPhy(int friendPhy) {
		this.friendPhy = friendPhy;
	}

	public int getFriendExplore() {
		return friendExplore;
	}

	public void setFriendExplore(int friendExplore) {
		this.friendExplore = friendExplore;
	}



	public PlayerTop transVo(){

		for (Map.Entry<Integer, Integer> entry : bossCheckPoint.entrySet()) {
			int checkId = entry.getKey();	//关卡ID
			int count = entry.getValue();	//剩余可用次数

			this.bossCheckPointStr += checkId + "," + count + "；";


			if(this.bossCheckPointStr.lastIndexOf(";") >0){
				this.bossCheckPointStr = this.bossCheckPointStr.substring(0,this.bossCheckPointStr.lastIndexOf(";"));
			}
		}

		for (Map.Entry<Integer, Integer> entry : generalCheckPoint.entrySet()) {
			int checkId = entry.getKey();	//关卡ID
			int count = entry.getValue();	//剩余可用次数

			this.generalCheckPointStr += checkId + "," + count + "；";
			if(this.generalCheckPointStr.lastIndexOf(";") >0){
				this.generalCheckPointStr = this.generalCheckPointStr.substring(0,this.generalCheckPointStr.lastIndexOf(";"));
			}
		}

		return this;
	}
}
