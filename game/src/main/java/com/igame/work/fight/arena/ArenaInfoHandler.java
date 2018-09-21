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

		int myRank = arenaService.getMRank(atype, player.getPlayerId());
		List<ArenaRanker> rank  = arenaService.rankOfType(atype);
		if(rank == null){
			rank = Lists.newArrayList();
		}
		List<ArenaRanker> opponent = Lists.newArrayList();	// 界面上现实的5个可挑战的对手
		if(!rank.isEmpty()){
			opponent = arenaService.randomOpponent(rank, myRank);
		}
		if(rank.size()>10){
			rank = rank.subList(0, 10);
		}

		rank.stream()	// TODO 可以重构成监听上下阵事件?战力变化?
				.filter(r->r.getPlayerId() == player.getPlayerId())
				.forEach(r->r.setFightValue(player.getTeams().get(6).getFightValue()
				));

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
