package com.igame.work.fight.arena;


import com.google.common.collect.Lists;
import com.igame.core.di.Inject;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ArenaInfoHandler extends ReconnectedHandler {

	@Inject private ArenaService arenaService;
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int atype = jsonObject.getInt("atype");

		List<ArenaRanker> opponent = Lists.newArrayList();
		int myRank = arenaService.getMRank(atype, player.getPlayerId());
		arenaService.setPlayerRank(player.getPlayerId(), myRank);
		List<ArenaRanker> rank  = arenaService.getRank(atype);
		if(rank == null){
			rank = Lists.newArrayList();
		}
		if(!rank.isEmpty()){
			opponent = ArenaService.getOpponent(rank, myRank);
			arenaService.setTempOpponent(player,opponent);
		}
		if(rank.size()>10){
			rank = rank.subList(0, 10);
		}
		for(ArenaRanker ar : rank) {	// TODO 可以重构成监听上下阵事件
			if(ar.getPlayerId() == player.getPlayerId()) {
				ar.setFightValue(player.getTeams().get(6).getFightValue());
				break;
			}
		}
		arenaService.setArenaType(player,atype);

		vo.addData("myRank", myRank);
		vo.addData("rank", rank);
		vo.addData("opponent", opponent);

		return vo;
	}

	@Override
	public int protocolId() {
		return MProtrol.AREA_INFO;
	}

}
