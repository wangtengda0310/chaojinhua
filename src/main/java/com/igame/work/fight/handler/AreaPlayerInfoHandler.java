package com.igame.work.fight.handler;


import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
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
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class AreaPlayerInfoHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {

		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}

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
			sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.AREA_P_INFO), vo, user);
			return;
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
				sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.AREA_P_INFO), vo, user);
				return;
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

		sendSucceed(MProtrol.toStringProtrol(MProtrol.AREA_P_INFO), vo, user);
	}

	
}
