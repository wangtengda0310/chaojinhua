package com.igame.work.monster.handler;



import net.sf.json.JSONObject;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.monster.service.MonsterService;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MonsterEVHandler extends BaseHandler{
	

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
		
		long objectId = jsonObject.getLong("objectId");
		int nextObject = jsonObject.getInt("nextObject");

		int ret = MonsterService.monsterEV(player, objectId, nextObject);
		
		if(ret != 0){
			vo.setState(1);
			vo.setErrCode(ret);
		}else{
			QuestService.processTask(player, 8, 1);
		}
		vo.addData("objectId", objectId);
		vo.addData("nextObject", nextObject);

		send(MProtrol.toStringProtrol(MProtrol.MONSTER_EV), vo, user);
	}

	
}
