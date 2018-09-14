package com.igame.work.turntable.dto;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.db.BasicDto;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Transient;

import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * @author xym
 *
 * 幸运大转盘
 */
@Entity(value = "turntables", noClassnameStored = true)
public class Turntable extends BasicDto {
	
	@Indexed
	private long playerId;//所属角色ID

	private Date lastUpdate;//上回刷新时间

	private Map<Integer,String> rewards = Maps.newHashMap();//道具 <位置,奖励字符串>

	private List<Integer> results = Lists.newArrayList();//结果

	@Transient
	private int dtate;//数据库状态 0-NO 1-新增 2-更新 3-删除

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Map<Integer, String> getRewards() {
		return rewards;
	}

	public void setRewards(Map<Integer, String> rewards) {
		this.rewards = rewards;
	}

	public List<Integer> getResults() {
		return results;
	}

	public void setResults(List<Integer> results) {
		this.results = results;
	}

	public String[] getRewardsStr() {
		String[] rewardsStr = new String[this.rewards.size()];

		int index = 0;
		for (Map.Entry<Integer, String> entry : rewards.entrySet()) {

			Integer site = entry.getKey();
			String item = entry.getValue();

			rewardsStr[index] = site + ";" + item;
			index++;
		}

		return rewardsStr;

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
