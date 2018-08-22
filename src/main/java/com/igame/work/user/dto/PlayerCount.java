package com.igame.work.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import com.igame.work.checkpoint.data.CheckPointTemplate;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.Map;


/**
 *
 * @author xym
 *
 * 	角色剩余次数
 *
 */
@Entity(noClassnameStored = true)
public class PlayerCount {

	@JsonIgnore
	private Map<Integer,Integer> bossCheckPoint = Maps.newHashMap();	//boss关卡可挑战次数

	@Transient
	private String bossCheckPointStr = "";	//boss关卡可挑战次数

	@JsonIgnore
	private Map<Integer,Integer> generalCheckPoint = Maps.newHashMap();	//一般关卡可挑战次数

	@Transient
	@JsonIgnore
	private String generalCheckPointStr = "";	//一般关卡可挑战次数

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




	public synchronized void addCheckPoint(Player player, CheckPointTemplate template, int value) {

		int chapterType = template.getChapterType();
		int chapterId = template.getChapterId();

		if (chapterType == 1){	//普通关卡

			int count = generalCheckPoint.get(chapterId) == null? value : generalCheckPoint.get(chapterId) + value;
			generalCheckPoint.put(chapterId,count);

			//MessageUtil.notiyCountChange(player,"generalCheckPoint",String.valueOf(chapterId),count);

		}else if (chapterType == 3){	//boss关卡

			int count = bossCheckPoint.get(chapterId) == null? value : bossCheckPoint.get(chapterId) + value;
			bossCheckPoint.put(chapterId,count);

		}
	}

	public int getCheckPoint(int chapterType, int chapterId) {

		int count = 0;
		if (chapterType == 1){	//普通关卡

			count = generalCheckPoint.get(chapterId) == null? 0 : generalCheckPoint.get(chapterId);
		}else if (chapterType == 3){	//boss关卡

			count = bossCheckPoint.get(chapterId) == null? 0 : bossCheckPoint.get(chapterId);
		}

		return count;
	}

	public PlayerCount transVo(){

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
