package com.igame.work.checkpoint.worldEvent.handler;





import com.igame.work.checkpoint.worldEvent.WorldEventDataManager;
import com.igame.work.checkpoint.worldEvent.WorldEventDto;
import net.sf.json.JSONObject;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.checkpoint.worldEvent.WorldEventTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class WorldEventListHandler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {

		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			return;
		}

		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);

		for(WorldEventTemplate ts : WorldEventDataManager.WorldEventData.getAll()){
			if(ts.getLevel() == 1 &&  MyUtil.hasCheckPoint(player.getCheckPoint(), String.valueOf(ts.getUnlock()))){
				player.getWordEvent().computeIfAbsent(ts.getEvent_type(),wet -> new WorldEventDto(player.getPlayerId(), ts.getEvent_type(), "", 0,1));
			}
		}	
		
		vo.addData("world", player.getWordEvent().values());

		send(MProtrol.toStringProtrol(MProtrol.WWORDEVENT_LIST), vo, user);
	}

	
}
