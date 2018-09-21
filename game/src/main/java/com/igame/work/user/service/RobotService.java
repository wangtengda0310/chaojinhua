package com.igame.work.user.service;

import com.google.common.collect.Lists;
import com.igame.core.ISFSModule;
import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.core.quartz.TimeListener;
import com.igame.util.GameMath;
import com.igame.work.fight.arena.ArenaService;
import com.igame.work.fight.arena.ArenadataTemplate;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.ComputeFightService;
import com.igame.work.monster.dto.Gods;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.user.dao.RobotDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.dto.Team;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    			rb = new RobotDto();
    			rb.setDtate(1);
    			rb.setSeverId(player.getSeverId());
    			rb.setPlayerId(player.getPlayerId());
    			rb.setLevel(player.getPlayerLevel());
    			rb.setName(player.getNickname());
    			rb.setPlayerFrameId(player.getPlayerFrameId());
    			rb.setPlayerHeadId(player.getPlayerHeadId());
    			rb.setFightValue(player.getFightValue());
    			Team team = player.getTeams().get(player.getCurTeam());
    			if(team != null){
    				Gods gods = player.getGods().get(team.getTeamGod());
    				if(gods != null){
    					rb.setGods(new GodsDto(gods));
    				}
    			}

				List<MatchMonsterDto> mon = Lists.newArrayList();

				// todo extract method
				long[] teamMonster = player.getTeams().get(player.getCurTeam()).getTeamMonster();
				for (int i = 0; i < teamMonster.length; i++) {
					Monster m = player.getMonsters().get(teamMonster[i]);
					MatchMonsterDto mto = new MatchMonsterDto(m, i);
					mto.reCalGods(player.callFightGods(), null);	// todo 这跟其他的不一样
					mon.add(mto);	// todo 这跟其他的不一样
				}

				rb.setMon(mon);

				robot.put(player.getUsername(), rb);
    		}
    	}
    	
    }

	@Override
	public void init(){
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
    public RobotDto createRobotDto(Player player,long playerId,String name,int level){
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
		long fightValue = 0;
		int aiLv1 = Integer.parseInt(config.getAiLv().split(",")[0]);
		int aiLv2 = Integer.parseInt(config.getAiLv().split(",")[1]);
		String[] split = config.getNum().split(",");
		int count = GameMath.getRandomCount(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
		String[] front = config.getFront().split(",");
		String[] back = config.getBack().split(",");
		StringBuilder monsterId = new StringBuilder();
		StringBuilder monsterLevel = new StringBuilder();
		for(int i = 1;i<=count;i++){
			String str = front[GameMath.getRandInt(front.length)];
			monsterId.append(",").append(str);
			monsterLevel.append(",").append(GameMath.getRandomCount(aiLv1, aiLv2));
		}
		count = 5- count;
		for(int i = 1;i<=count;i++){
			String str = back[GameMath.getRandInt(back.length)];
			monsterId.append(",").append(str);
			monsterLevel.append(",").append(GameMath.getRandomCount(aiLv1, aiLv2));
		}
		if(monsterId.length() > 0){
			monsterId = new StringBuilder(monsterId.substring(1));
		}
		if(monsterLevel.length() > 0){
			monsterLevel = new StringBuilder(monsterLevel.substring(1));
		}

		// todo extract method 跟别的方法不一样
		Map<Long, Monster> monster = monsterService.batchCreateMonster(monsterId.toString(), monsterLevel.toString(), "", "","");
		for( Map.Entry<Long, Monster> entry:monster.entrySet()) {
			int i = entry.getKey().intValue();
			Monster m = entry.getValue();
			MatchMonsterDto mto = new MatchMonsterDto(m, i);
			computeFightService.computeMonsterFight(m);
			mto.reCalGods(player.callFightGods(), null);
			rto.getMon().add(mto);
			fightValue += m.getFightValue();
		}

		rto.setFightValue(fightValue);
    	return rto;
    }
    

	public static RobotDto createRobotLike(Player player) {
		Team team = player.getTeams().get(6);

		RobotDto rb;
		rb = new RobotDto();
		rb.setDtate(1);
		rb.setSeverId(player.getSeverId());
		rb.setPlayerId(player.getPlayerId());
		rb.setLevel(player.getPlayerLevel());
		rb.setName(player.getNickname());
		rb.setPlayerFrameId(player.getPlayerFrameId());
		rb.setPlayerHeadId(player.getPlayerHeadId());
		rb.setFightValue(team.getFightValue());

		//防守阵容神灵
		Gods gods = player.getGods().get(team.getTeamGod());
		if(gods != null){
			rb.setGods(new GodsDto(gods));
		}

		List<MatchMonsterDto> mon = Lists.newArrayList();

		// todo extract method
		long[] teamMonster = team.getTeamMonster();
		for (int i = 0; i < teamMonster.length; i++) {
			Monster m = player.getMonsters().get(teamMonster[i]);
			MatchMonsterDto mto = new MatchMonsterDto(m, i);
			mto.reCalGods(player.callFightGods(), null);
			mon.add(mto);
		}

		rb.setMon(mon);
		rb.setType(1);
		return rb;
	}
    

}
