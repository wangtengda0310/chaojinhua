package com.igame.work.fight.arena;


import com.google.common.collect.Lists;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.ErrorCode;
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
public class ArenaRefHandler extends ReconnectedHandler {

	private ArenaService arenaService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
		List<ArenaRanker> opponent = Lists.newArrayList();
		int arenaType = arenaService.getArenaType(player);
		List<ArenaRanker> rank = arenaService.getRank(arenaType);
		if(arenaType < 1 || arenaType > 9 || rank == null){
			return error(ErrorCode.ERROR);
		}else{
			if(!rank.isEmpty()){
				int playerRank = arenaService.getPlayerRank(player.getPlayerId());	// !rank.isEmpty()
				opponent = arenaService.getOpponent(rank, playerRank);
				arenaService.setChallenge(player,opponent);
			}
		}

		RetVO vo = new RetVO();
		vo.addData("opponent", opponent);
		return vo;
	}

	@Override
	public int protocolId() {
		return MProtrol.AREA_REF;
	}

}
