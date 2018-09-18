package com.igame.work.item.dto;

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
@Entity(value = "Item", noClassnameStored = true)
public class Item   extends BasicDto {
	
	@Indexed
	@JsonIgnore
	public long playerId;//所属角色ID
	
	private int itemId;//道具ID
	
	private int count;//叠加数

	@JsonIgnore
	private int[] equipCounts = new int[]{0,0,0,0,0,0};//装备数
	
	@Transient
	@JsonIgnore
	public int dtate;//数据库状态 0-NO 1-新增 2-更新 3-删除
	
	
	public Item(){
		
	}

	public Item(long playerId, int itemId, int count, int dtate) {
		super();
		this.playerId = playerId;
		this.itemId = itemId;
		this.count = count;
		this.dtate = dtate;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int[] getEquipCounts() {
		return equipCounts;
	}

	public void setEquipCounts(int[] equipCounts) {
		this.equipCounts = equipCounts;
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

	/**
	 * 获取可用个数
	 */
	public int getUsableCount(int teamId) {

		if (teamId != -1 && (teamId < 1 || 6 < teamId))
			return 0;

		int used;
		if (teamId == -1){
			used = this.equipCounts[0];
			for (int equipcount : equipCounts) {
				if (equipcount > used)
					used = equipcount;
			}

		}else {

			used = this.equipCounts[teamId-1];

		}

		return this.count - used;
	}

	public void addCount(int value) {
		this.count += value;
	}
}
