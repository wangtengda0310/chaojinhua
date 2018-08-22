package com.igame.work.fight.handler;


import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

/**
 * 
 * @author xym
 *
 * 	购买竞技场挑战次数
 *
 */
public class AreaBuyHandler extends BaseHandler{
	

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

		//校验钻石
		int diamond = player.getDiamond();
		if (diamond < 50){
			sendError(ErrorCode.DIAMOND_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.AREA_BUY), vo, user);
			return;
		}

		//减少钻石
		ResourceService.ins().addDiamond(player,-25);

		//减少已挑战
		player.addAreaCount(-5);

		vo.addData("areaCount",player.getAreaCount());
		sendSucceed(MProtrol.toStringProtrol(MProtrol.AREA_BUY), vo, user);
	}

	
}
