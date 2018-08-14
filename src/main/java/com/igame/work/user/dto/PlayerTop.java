package com.igame.work.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.CheckPointTemplate;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.List;
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

	public PlayerTop init(){

		//初始化关卡挑战次数
		List<CheckPointTemplate> all = DataManager.CheckPointData.getAll();
		for (CheckPointTemplate template : all) {
			int chapterId = template.getChapterId();
			int chapterType = template.getChapterType();
			Integer count = template.getCount();

			if (count == null)
				continue;

			if (chapterType == 1){	//普通关卡
				generalCheckPoint.put(chapterId,count);
			}else if (chapterType == 3){	//boss关卡
				bossCheckPoint.put(chapterId,count);
			}
		}

		this.friendPhy = 20;
		this.friendExplore = 20;
		this.friendCount = 20;

		return this;
	}

	@JsonIgnore
	private Map<Integer,Integer> bossCheckPoint = Maps.newHashMap();	//boss关卡挑战上限

	@Transient
	private String bossCheckPointStr = "";	//boss关卡挑战上限

	@JsonIgnore
	private Map<Integer,Integer> generalCheckPoint = Maps.newHashMap();	//一般关卡挑战上限

	@Transient
	@JsonIgnore
	private String generalCheckPointStr = "";	//一般关卡挑战上限

	private int friendPhy;	//好友体力领取上限

	private int friendExplore;	//好友探索加速上限

	private int friendCount;	//好友数量上限


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

	public int getFriendCount() {
		return friendCount;
	}

	public void setFriendCount(int friendCount) {
		this.friendCount = friendCount;
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
