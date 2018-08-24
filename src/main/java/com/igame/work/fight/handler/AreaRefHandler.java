package com.igame.work.fight.handler;


import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
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
public class AreaRefHandler extends ReconnectedHandler {
	

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		
		List<AreaRanker> opponent = Lists.newArrayList();
		List<AreaRanker> rank = ArenaService.ins().getRank(player.getAreaType(), player.getSeverId());
		if(player.getAreaType() < 1 || player.getAreaType() > 9 || rank == null){
			return error(ErrorCode.ERROR);
		}else{
			if(!rank.isEmpty()){
				opponent = ArenaService.ins().getOpponent(rank, player.getMyRank());
				player.setTempOpponent(opponent);
			}
		}

		RetVO vo = new RetVO();
		vo.addData("opponent", opponent);
		return vo;
	}

	@Override
	protected int protocolId() {
		return MProtrol.AREA_REF;
	}

}
