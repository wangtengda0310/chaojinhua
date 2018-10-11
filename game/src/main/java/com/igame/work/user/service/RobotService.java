package com.igame.work.user.service;

import com.igame.core.ISFSModule;
import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.core.quartz.TimeListener;
import com.igame.util.GameMath;
import com.igame.work.fight.arena.ArenaService;
import com.igame.work.fight.arena.ArenadataTemplate;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dao.RobotDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;

import java.util.*;

/**
 * 
 * @author Marcus.Z
 *
 */
public class RobotService extends EventService implements ISFSModule, TimeListener {
	@Inject private ComputeFightService computeFightService;
	@Inject private RobotDAO dao;
	@Inject private SessionManager sessionManager;
	@Inject private ArenaService arenaService;
	@Inject private MonsterService monsterService;

	@Override
	public void minute5() {
		ref();
		save();
	}

	private static Map<String,RobotDto> robot;
    
    
    private void ref(){
    	for(Player player : sessionManager.getSessions().values()){
			RobotDto rb = robot.get(player.getNickname());
    		if(rb == null){
				robot.put(player.getUsername(), player.robotOfTeam(player.getCurTeam()));
    		}
    	}
    	
    }

	@Override
	public void init() {
    	super.init();

		robot = dao.loadData();
		if (robot == null) {
			robot = new HashMap<>();
		}
	}
    
    private void save(){
		for(RobotDto m : robot.values()){
			try{
				dao.update(m);
			}catch(Exception e){
				e.printStackTrace();
			}

		}
    }

	public Map<String, RobotDto> getRobot() {
		return robot;
	}

    /**
     * 生成机器人数据
     */
    public RobotDto createArenaRobotDto(Player player, long playerId, String name, int level){
    	ArenadataTemplate config = arenaService.arenaData.getTemplateByPlayerLevel(player.getPlayerLevel());
    	if(config == null){
    		return null;
		}
		RobotDto rto = new RobotDto();
		rto.setSeverId(player.getSeverId());
		rto.setPlayerId(playerId);
		rto.setName(name);
		rto.setLevel(level);
		rto.setType(1);

		List<Monster> monsters = monsterService.createMonsterOfAll(randomOne(config.getMonsterset(),"\\|"));
        long fightValue = 0;
		for( Monster mto:monsters) {
			computeFightService.computeMonsterFight(mto);   // todo move to somewhere where can auto calc it
			rto.getMon().add(mto.toMatchMonsterDto());
            fightValue += mto.getFightValue();
		}

        rto.setFightValue(fightValue);
    	return rto;
    }

    public int randomOne(String str, String splitChar) {
        String[] split = str.split(splitChar);
        int r = GameMath.getRandInt(split.length);
        return Integer.parseInt(split[r]);
    }


}
