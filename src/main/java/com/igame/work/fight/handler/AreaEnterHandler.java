package com.igame.work.fight.handler;




import net.sf.json.JSONObject;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.fight.dto.AreaRanker;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class AreaEnterHandler extends BaseHandler{
	

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
		vo.addData("playerId", playerId);

		//校验挑战次数
		if(player.getAreaCount() >= 5){
			sendError(ErrorCode.TODAY_COUNT_NOTENOUGH,MProtrol.toStringProtrol(MProtrol.AREA_ENTER), vo, user);
			return;
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
			sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.AREA_ENTER), vo, user);
			return;
		}


		if(oter.getPlayerId() == player.getPlayerId()){
			sendError(ErrorCode.AREA_NOT_SELF,MProtrol.toStringProtrol(MProtrol.AREA_ENTER), vo, user);
			return;
		}

		if(oter.getRank()>=player.getMyRank()){
			sendError(ErrorCode.AREA_NOT_LOW,MProtrol.toStringProtrol(MProtrol.AREA_ENTER), vo, user);
			return;
		}

		player.setTempAreaPlayerId(playerId);
		ResourceService.ins().addAreaCount(player, 1);

		send(MProtrol.toStringProtrol(MProtrol.AREA_ENTER), vo, user);
	}

	
}
