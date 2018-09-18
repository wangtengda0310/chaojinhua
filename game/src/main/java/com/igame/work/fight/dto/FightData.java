package com.igame.work.fight.dto;

import java.util.Map;

import com.google.common.collect.Maps;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;

/**
 * 
 * @author Marcus.Z
 *
 */
public class FightData {
	
	public Player player;
	
	public long playerId;
	
	public int type; // 0-PVE中敌方  1-玩家
	
	public Map<Long,Monster> monsters = Maps.newHashMap();
	
	public FightData(){
		
	}
	
	public FightData(Player player){
		
		if(player != null){
			this.player = player;
			this.playerId = player.getPlayerId();
			this.type = 1;
			//String[] mms = player.getTeams()[0].split(",");
			long[] mms = player.getTeams().get(player.getCurTeam()).getTeamMonster();
			int i = 1;
			for(long mid : mms){
				if(-1 != mid && 0 != mid){
					Monster mm = player.getMonsters().get(mid);
					if(mm != null){	
						mm.resetFightProp(i);
						monsters.put(mm.getObjectId(), mm);
						i++;
						if(i > 5){
							i = 1;
						}
					}
				}
			}
		}
		
	}

	public FightData(Player player, Map<Long, Monster> monsters) {
		this.player = player;
		this.type = 0;
		this.monsters = monsters;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Map<Long, Monster> getMonsters() {
		return monsters;
	}

	public void setMonsters(Map<Long, Monster> monsters) {
		this.monsters = monsters;
	}

	public long getPlayerId() {
		return playerId;
	}

	public void setPlayerId(long playerId) {
		this.playerId = playerId;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
	
	
	
	
	

}
