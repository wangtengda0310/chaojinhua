package com.igame.work.item.handler;



import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ItemHandler extends BaseHandler{
	

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
		int itemId = jsonObject.getInt("itemId");
		int count = jsonObject.getInt("count");
		int targetType = jsonObject.getInt("targetType");
		long targetId = jsonObject.getLong("targetId");
		
		int ret = ResourceService.ins().useItem(player, itemId, count, targetType, targetId);
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}
		vo.addData("itemId", itemId);
		vo.addData("count", count);
		vo.addData("targetType", targetType);
		vo.addData("targetId", targetId);
//		vo.addData("test", player.getMonsters().get(targetId).ff);

		send(MProtrol.toStringProtrol(MProtrol.ITEM_USE), vo, user);
	}

	
}
