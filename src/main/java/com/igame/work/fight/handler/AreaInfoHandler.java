package com.igame.work.fight.handler;


import com.google.common.collect.Lists;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.MProtrol;
import com.igame.work.fight.dto.AreaRanker;
import com.igame.work.fight.service.ArenaService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * 
 * @author Marcus.Z
 *
 */
public class AreaInfoHandler extends ReconnectedHandler {

	private ArenaService arenaService;
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		int atype = jsonObject.getInt("atype");

		List<AreaRanker> opponent = Lists.newArrayList();
		int myRank = arenaService.getMRank(atype, player.getSeverId(), player.getPlayerId());
		player.setMyRank(myRank);
		List<AreaRanker> rank  = arenaService.getRank(atype, player.getSeverId());
		if(rank == null){
			rank = Lists.newArrayList();
		}
		if(!rank.isEmpty()){
			opponent = ArenaService.getOpponent(rank, myRank);
			player.setTempOpponent(opponent);
		}
		if(rank.size()>10){
			rank = rank.subList(0, 10);
		}
		for(AreaRanker ar : rank) {	// TODO 可以重构成监听上下阵事件
			if(ar.getPlayerId() == player.getPlayerId()) {
				ar.setFightValue(player.getTeams().get(6).getFightValue());
				break;
			}
		}
		player.setAreaType(atype);

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
