package com.igame.work.fight.arena;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dao.MonsterDAO;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.RobotDto;
import com.igame.work.user.service.RobotService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ArenaPlayerInfoHandler extends ReconnectedHandler {

	@Inject
	private ArenaService arenaService;
	@Inject private PlayerDAO playerDAO;
	@Inject private RobotService robotService;
	@Inject private MonsterDAO monsterDAO;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		long playerId = jsonObject.getLong("playerId");


		String name = "";
		int level = 0;
		int playerFrameId = 0;
		int playerHeadId = 0;
//		long fightValue = 0;
		GodsDto gods = new GodsDto();
		List<MatchMonsterDto> lb = Lists.newArrayList();
		new RobotDto();
		Map<Long,RobotDto> map = arenaService.getRobot();
//		List<ArenaRanker> rank = arenaServiceDto.getRankDto(player.getAreaType(), player.getSeverId());
		ArenaRanker oter = null;
		for(ArenaRanker ar : player.getTempOpponent()){
			if(ar.getPlayerId() == playerId){
				oter = ar;
				break;
			}
		}
		if(map == null){
			return error(ErrorCode.ERROR);
		}
		RobotDto rto = map.get(playerId);

		Player opponent = playerDAO.getPlayerByPlayerId(playerId);
		if(opponent != null){	// TODO 监听上下阵事件
			Map<Long, Monster> mons = monsterDAO.getMonsterByPlayer(opponent, opponent.getPlayerId());
			opponent.setMonsters(mons);
			rto = RobotService.createRobotLike(opponent);
			
			gods = rto.getGods();
			for(MatchMonsterDto mo : rto.getMon()){//处理神灵加成属性
				MatchMonsterDto mto = mo.clonew();
				mto.reCalGods(player.callFightGods(), gods);
				lb.add(mto);
			}
			level = rto.getLevel();
			playerFrameId = rto.getPlayerFrameId();
			playerHeadId = rto.getPlayerHeadId();
		}else if(rto == null){
			if(playerId >= 100011){
				return error(ErrorCode.ERROR);
			}

			if(oter != null){
				rto = robotService.createRobotDto(player, playerId, oter.getName(), 2);
				gods = rto.getGods();
				for(MatchMonsterDto mo : rto.getMon()){//处理神灵加成属性
					MatchMonsterDto mto = mo.clonew();
					mto.reCalGods(player.callFightGods(), gods);
					lb.add(mto);
				}
				level = rto.getLevel();
				playerFrameId = rto.getPlayerFrameId();
				playerHeadId = rto.getPlayerHeadId();
			}


		}else{
			gods = rto.getGods();
			for(MatchMonsterDto mo : rto.getMon()){//处理神灵加成属性
				MatchMonsterDto mto = mo.clonew();
				mto.reCalGods(player.callFightGods(), gods);
				lb.add(mto);
			}
			level = rto.getLevel();
			playerFrameId = rto.getPlayerFrameId();
			playerHeadId = rto.getPlayerHeadId();
		}


		if(oter != null){
			name = oter.getName();
			
		}
		if(rto == null){
			rto = new RobotDto();
		}

		vo.addData("playerId", playerId);
		vo.addData("name", name);
		vo.addData("level", level);
		vo.addData("playerFrameId", playerFrameId);
		vo.addData("playerHeadId", playerHeadId);
		vo.addData("fightValue", rto.getFightValue());
		vo.addData("gods", gods);
		vo.addData("m", lb);

		return vo;
	}

	@Override
	public int protocolId() {
		return MProtrol.AREA_P_INFO;
	}

}
