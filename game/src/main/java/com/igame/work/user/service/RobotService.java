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
		throw new UnsupportedOperationException("怪物这里调用怪物组模块生成怪物");
//		long fightValue = 0;
//		int aiLv1 = Integer.parseInt(config.getAiLv().split(",")[0]);
//		int aiLv2 = Integer.parseInt(config.getAiLv().split(",")[1]);
//		String[] split = config.getNum().split(",");
//		int count = GameMath.getRandomCount(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
//		String[] front = config.getFront().split(",");
//		String[] back = config.getBack().split(",");
//		List<String> monsterId = new LinkedList<>();
//		List<Integer> monsterLevel = new LinkedList<>();
//		for(int i = 1;i<=count;i++){
//			String str = front[GameMath.getRandInt(front.length)];
//			monsterId.add(str);
//			monsterLevel.add(GameMath.getRandomCount(aiLv1, aiLv2));
//		}
//		count = 5- count;
//		for(int i = 1;i<=count;i++){
//			String str = back[GameMath.getRandInt(back.length)];
//			monsterId.add(str);
//			monsterLevel.add(GameMath.getRandomCount(aiLv1, aiLv2));
//		}
//
//		// todo extract method 跟别的方法不一样
//		Map<Long, Monster> monster = monsterService.batchCreateMonster(monsterId, monsterLevel, "", Collections.emptyList(),Collections.emptyList());
//		for( Map.Entry<Long, Monster> entry:monster.entrySet()) {
//			int i = entry.getKey().intValue();
//			Monster m = entry.getValue();
//			MatchMonsterDto mto = new MatchMonsterDto(m, i);
//			computeFightService.computeMonsterFight(m);
//			mto.reCalGods(player.currentFightGods(), null);
//			rto.getMon().add(mto);
//			fightValue += m.getFightValue();
//		}
//
//		rto.setFightValue(fightValue);
//    	return rto;
    }
    


}
