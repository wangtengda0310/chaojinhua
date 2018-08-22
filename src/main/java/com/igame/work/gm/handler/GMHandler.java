package com.igame.work.gm.handler;



import net.sf.json.JSONObject;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.gm.service.GMService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;


/**
 * 
 * @author Marcus.Z
 *
 */
public class GMHandler extends BaseHandler{
	

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
		String gm = jsonObject.getString("gm");
		boolean succ = GMService.processGM(player, gm);

		if(!succ){
			vo.setState(1);
			vo.setErrCode(1);
		}
		send(MProtrol.toStringProtrol(MProtrol.GM), vo, user);
	}

	
}
