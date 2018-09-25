package com.igame.work.fight.arena;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.fight.dto.GodsDto;
import com.igame.work.fight.dto.MatchMonsterDto;
import com.igame.work.monster.dto.Monster;
import com.igame.work.monster.service.MonsterService;
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
	@Inject private MonsterService monsterService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		long opponentPlayerId = jsonObject.getLong("playerId");


		String name = "";
		int level = 0;
		int playerFrameId = 0;
		int playerHeadId = 0;
//		long fightValue = 0;
		RobotDto rto;

		Player opponent = playerDAO.getPlayerByPlayerId(opponentPlayerId);
		if(opponent != null){	// TODO 监听上下阵事件?
			Map<Long, Monster> mons = monsterService.getMonsterByPlayer(opponent);
			opponent.setMonsters(mons);
			rto = opponent.robotOfDefence();

		}else{
			rto = arenaService.getRobot().get(opponentPlayerId);

			if (rto == null && opponentPlayerId >= 100011) {    // todo 这个100011啥意思
				return error(ErrorCode.ERROR);
			} else {
				ArenaRanker opponentRanker = arenaService.getRankInfo(player);

				if (opponentRanker != null) {
					rto = robotService.createArenaRobotDto(player, opponentPlayerId, opponentRanker.getName(), 2);


					name = opponentRanker.getName();

				}
			}
		}
		if(rto == null){
			rto = new RobotDto();
		}
		GodsDto gods = rto.getGods();
		List<MatchMonsterDto> lb = Lists.newArrayList();
		for (MatchMonsterDto mo : rto.getMon()) {//处理神灵加成属性
			MatchMonsterDto mto = mo.clonew(player.currentFightGods(), gods);
			lb.add(mto);
		}

		level = rto.getLevel();
		playerFrameId = rto.getPlayerFrameId();
		playerHeadId = rto.getPlayerHeadId();


		vo.addData("playerId", opponentPlayerId);
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
