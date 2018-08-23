package com.igame.work.checkpoint.guanqia;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class RewardDto {
	
	public int ret;
	
	public int exp;
	
	public long gold;
	
	public int diamond;
	
	public int physical;
	
	public int tongRes;
	
	public Map<Integer,Integer> items = Maps.newHashMap();
	
	public Map<Integer,Integer> monsters = Maps.newHashMap();
	

	public int getRet() {
		return ret;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}
	
	public void addExp(int exp) {
		this.exp += exp;
	}

	public long getGold() {
		return gold;
	}

	public void setGold(long gold) {
		this.gold = gold;
	}
	
	public void addGold(long gold) {
		this.gold += gold;
	}

	public Map<Integer, Integer> getItems() {
		return items;
	}

	public void setItems(Map<Integer, Integer> items) {
		this.items = items;
	}

	public Map<Integer, Integer> getMonsters() {
		return monsters;
	}

	public void setMonsters(Map<Integer, Integer> monsters) {
		this.monsters = monsters;
	}

	public int getDiamond() {
		return diamond;
	}

	public void setDiamond(int diamond) {
		this.diamond = diamond;
	}
	
	public void addDiamond(int diamond) {
		this.diamond += diamond;
	}

	public int getPhysical() {
		return physical;
	}

	public void setPhysical(int physical) {
		this.physical = physical;
	}
	
	public void addPhysical(int physical) {
		this.physical += physical;
	}
	
	
	public void addItem(int itemId,int count){
		if(items.get(itemId) == null){
			items.put(itemId, count);
		}else{
			items.put(itemId, items.get(itemId) + count);
		}
	}
	
	public void addMonster(int mmId,int count){
		if(monsters.get(mmId) == null){
			monsters.put(mmId, count);
		}else{
			monsters.put(mmId, monsters.get(mmId) + count);
		}
	}

	public int getTongExp() {
		return tongRes;
	}

	public void setTongExp(int tongRes) {
		this.tongRes = tongRes;
	}
	
	public void addTongExp(int tongRes) {
		this.tongRes += tongRes;
	}
	
	
	
	
	

}
