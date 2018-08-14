package com.igame.work.user.service;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.ArenadataTemplate;
import com.igame.core.db.DBManager;
import com.igame.util.GameMath;
import com.igame.work.fight.dto.FightBase;
import com.igame.work.fight.dto.FightData;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.fight.service.FightUtil;
import com.igame.work.monster.dto.Gods;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dao.RobotDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.dto.Team;

/**
 * 
 * @author Marcus.Z
 *
 */
public class RobotService {
	
    private static final RobotService domain = new RobotService();

    public static final RobotService ins() {
        return domain;
    }
    
    public Map<Integer,Map<String,RobotDto>> robot = Maps.newHashMap();
    
    
    public void ref(){
    	for(Player player : SessionManager.ins().getSessions().values()){
    		Map<String,RobotDto> map = robot.get(player.getSeverId());
    		if(map == null){
    			map = Maps.newHashMap();
    			robot.put(player.getSeverId(), map);
    		}
    		RobotDto rb = map.get(player.getNickname());
    		if(rb == null){
    			rb = new RobotDto();
    			rb.setDtate(1);
    			rb.setSeverId(player.getSeverId());
    			rb.setPlayerId(player.getPlayerId());
    			rb.setLevel(player.getPlayerLevel());
    			rb.setName(player.getNickname());
    			rb.setPlayerFrameId(player.getPlayerFrameId());
    			rb.setPlayerHeadId(player.getPlayerHeadId());
    			rb.setFightValue(player.getFightValue());
    			FightData fd = new FightData(player);
    			Team team = player.getTeams().get(player.getCurTeam());
    			if(team != null){
    				Gods gods = player.getGods().get(team.getTeamGod());
    				if(gods != null){
    					rb.setGods(new GodsDto(gods));
    				}
    			}
    			List<MatchMonsterDto> mon = Lists.newArrayList();
    	    	for(Monster m : fd.getMonsters().values()){
    	    		MatchMonsterDto mto = new MatchMonsterDto(m);
    	    		mon.add(mto);
    	    	}
    			rb.setMon(mon);

    			map.put(player.getUsername(), rb);
    		}
    	}
    	
    }
    
    public void load(){
		String DBName = DBManager.getInstance().p.getProperty("DBName");
		String[] DBNames = DBName.split(",");
		for(String db : DBNames){
			int serverId=Integer.parseInt(db.substring(5));
			robot.put(serverId, RobotDAO.ins().loadData(serverId));
		}
    }
    
    public void save(){
		for(Map<String,RobotDto> db : robot.values()){
			try{
				for(RobotDto m : db.values()){
					RobotDAO.ins().update(m);
				}
			}catch(Exception e){
				e.printStackTrace();
			}

		}
    }

	public Map<Integer, Map<String, RobotDto>> getRobot() {
		return robot;
	}

	public void setRobot(Map<Integer, Map<String, RobotDto>> robot) {
		this.robot = robot;
	}
    

    /**
     * 生成机器人数据
     * @param player
     * @return
     */
    public RobotDto createRobotDto(Player player,long playerId,String name,int level){
    	RobotDto rto = null;
    	ArenadataTemplate at = DataManager.ArenaData.getTemplateByPlayerLevel(player.getPlayerLevel());
    	if(at != null){
    		rto = new RobotDto();
    		rto.setSeverId(player.getSeverId());
    		rto.setPlayerId(playerId);
    		rto.setName(name);
    		rto.setLevel(level);
    		rto.setType(1);
    		long fightValue = 0;
    		int aiLv1 = Integer.parseInt(at.getAiLv().split(",")[0]);
    		int aiLv2 = Integer.parseInt(at.getAiLv().split(",")[1]);
    		int count = GameMath.getRandomCount(Integer.parseInt(at.getNum().split(",")[0]), Integer.parseInt(at.getNum().split(",")[1]));
    		String[] fornt = at.getFront().split(",");
    		String[] back = at.getBack().split(",");
    		String monsterId = "";
    		String monsterLevel = "";
    		for(int i = 1;i<=count;i++){
    			monsterId += "," + fornt[GameMath.getRandInt(fornt.length)];
    			monsterLevel += "," +GameMath.getRandomCount(aiLv1,aiLv2);
    		}
    		count = 5- count;
    		for(int i = 1;i<=count;i++){
    			monsterId += "," + back[GameMath.getRandInt(fornt.length)];
    			monsterLevel += "," + GameMath.getRandomCount(aiLv1,aiLv2);
    		}
    		if(monsterId.length() > 0){
    			monsterId = monsterId.substring(1);
    		}
    		if(monsterLevel.length() > 0){
    			monsterLevel = monsterLevel.substring(1);
    		}
			FightBase fb  = new FightBase(player.getPlayerId(),new FightData(player),new FightData(null,FightUtil.createMonster(monsterId, monsterLevel, "", "","")));
			
	    	for(Monster m : fb.getFightB().getMonsters().values()){
	    		MatchMonsterDto mto = new MatchMonsterDto(m);
	    		rto.getMon().add(mto);
				ComputeFightService.ins().computeMonsterFight(m);
				//m.reCalFightValue();
				fightValue += m.getFightValue();
	    	}
	    	rto.setFightValue(fightValue);
    	}
    	return rto;
    }
    

	public RobotDto createRobotLike(Player player) {
		RobotDto rb;
		rb = new RobotDto();
		rb.setDtate(1);
		rb.setSeverId(player.getSeverId());
		rb.setPlayerId(player.getPlayerId());
		rb.setLevel(player.getPlayerLevel());
		rb.setName(player.getNickname());
		rb.setPlayerFrameId(player.getPlayerFrameId());
		rb.setPlayerHeadId(player.getPlayerHeadId());
		rb.setFightValue(player.getTeams().get(6).getFightValue());
		Map<Long,Monster> monsters = Maps.newHashMap();
		long[] mms = player.getTeams().get(6).getTeamMonster();
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
		
		//防守阵容神灵
		Team team = player.getTeams().get(6);
		if(team != null){
			Gods gods = player.getGods().get(team.getTeamGod());
			if(gods != null){
				rb.setGods(new GodsDto(gods));
			}
		}
		List<MatchMonsterDto> mon = Lists.newArrayList();
		for(Monster m : monsters.values()){
			MatchMonsterDto mto = new MatchMonsterDto(m);
			mon.add(mto);
		}
		rb.setMon(mon);
		rb.setType(1);
		return rb;
	}
    

}
