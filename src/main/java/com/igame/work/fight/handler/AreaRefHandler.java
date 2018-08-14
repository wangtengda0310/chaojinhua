package com.igame.work.fight.handler;






import java.util.List;

import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.GameMath;
import com.igame.work.fight.dto.AreaRanker;
import com.igame.work.fight.service.ArenaService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class AreaRefHandler extends BaseHandler{
	

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
		
		int ret = 0;
		List<AreaRanker> opponent = Lists.newArrayList();
		List<AreaRanker> rank = ArenaService.ins().getRank(player.getAreaType(), player.getSeverId());
		if(player.getAreaType() < 1 || player.getAreaType() > 9 || rank == null){
			ret = ErrorCode.ERROR;
		}else{
			if(!rank.isEmpty()){
				opponent = ArenaService.ins().getOpponent(rank, player.getMyRank());
				player.setTempOpponent(opponent);
			}
		}

		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("opponent", opponent);

		send(MProtrol.toStringProtrol(MProtrol.AREA_REF), vo, user);
	}

	
}
