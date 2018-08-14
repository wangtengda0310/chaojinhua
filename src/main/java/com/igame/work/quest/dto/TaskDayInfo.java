package com.igame.work.quest.dto;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.QuestTemplate;
import com.igame.core.db.BasicVO;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;


/**
 * 
 * @author zhuanghaohui
 *
 */
@Entity(value = "Quest", noClassnameStored = true)
public class TaskDayInfo   extends BasicVO {
	
	@Indexed
	@JsonIgnore
	public long playerId;//所属角色ID
	
	private int questId;//任务ID
	
	@Transient
	private int action;//操作类型 1-新增 2-更新 3-删除
	
	private int vars;//当前进度
	
	private int status = 1;//状态  1-未完成，2-可领取，3-已完成
	
	@Transient
	@JsonIgnore
	public int dtate;//数据库状态 0-NO 1-新增 2-更新 3-删除
	

	
	public TaskDayInfo(){
		super();
	}
	


	public TaskDayInfo(Player player,int questId) {
		super();
		this.playerId = player.getPlayerId();
		this.questId = questId;
		this.dtate = 1;
		this.action = 1;
		QuestTemplate qt = DataManager.QuestData.getTemplate(questId);
		if(qt.getQuestType()==2 && qt.getClaim()>1 && qt.getClaim()<=25){
			QuestService.processTaskDetail(player, Lists.newArrayList(), this, qt.getClaim(), 0);
		}
		
	}
	

	public int getQuestId() {
		return questId;
	}



	public void setQuestId(int questId) {
		this.questId = questId;
	}



	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getVars() {
		return vars;
	}

	public void setVars(int vars) {
		this.vars = vars;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		if(this.action == 1 && action == 2){
			return;
		}
		this.action = action;
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



	public int getDtate() {
		return dtate;
	}



	public long getPlayerId() {
		return playerId;
	}



	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}
	
	

}
