package com.igame.work.fight.handler;


import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.fight.dto.AreaRanker;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class AreaEnterHandler extends ReconnectedHandler {

	private ResourceService resourceService;

	@Override
	protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		long playerId = jsonObject.getLong("playerId");
		vo.addData("playerId", playerId);

		//校验挑战次数
		if(player.getAreaCount() >= 5){
			return error(ErrorCode.TODAY_COUNT_NOTENOUGH);
		}

		//校验对手
		AreaRanker oter = null;
		for(AreaRanker ar : player.getTempOpponent()){
			if(ar.getPlayerId() == playerId){
				oter = ar;
				break;
			}
		}
		if(oter == null){
			return error(ErrorCode.ERROR);
		}


		if(oter.getPlayerId() == player.getPlayerId()){
			return error(ErrorCode.AREA_NOT_SELF);
		}

		if(oter.getRank()>=player.getMyRank()){
			return error(ErrorCode.AREA_NOT_LOW);
		}

		player.setTempAreaPlayerId(playerId);
		resourceService.addAreaCount(player, 1);

		return vo;
	}

	@Override
    public int protocolId() {
		return MProtrol.AREA_ENTER;
	}

}
