package com.igame.work.fight.handler;


import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.fight.dto.AreaRanker;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.fight.service.ArenaService;
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
public class AreaPlayerInfoHandler extends ReconnectedHandler {
	

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
		Map<Long,RobotDto> map = ArenaService.ins().getRobot().get(player.getSeverId());
//		List<AreaRanker> rank = ArenaService.ins().getRank(player.getAreaType(), player.getSeverId());
		AreaRanker oter = null;
		for(AreaRanker ar : player.getTempOpponent()){
			if(ar.getPlayerId() == playerId){
				oter = ar;
				break;
			}
		}
		if(map == null){
			return error(ErrorCode.ERROR);
		}
		RobotDto rto = map.get(playerId);

		Player opponent = PlayerDAO.ins().getPlayerByPlayerId(player.getSeverId(), playerId);
		if(opponent != null){	// TODO 监听上下阵事件
			Map<Long, Monster> mons = MonsterDAO.ins().getMonsterByPlayer(opponent,opponent.getSeverId(), opponent.getPlayerId());
			opponent.setMonsters(mons);
			rto = RobotService.ins().createRobotLike(opponent);
			
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
				rto = RobotService.ins().createRobotDto(player, playerId, oter.getName(), 2);
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
	protected int protocolId() {
		return MProtrol.AREA_P_INFO;
	}

}
