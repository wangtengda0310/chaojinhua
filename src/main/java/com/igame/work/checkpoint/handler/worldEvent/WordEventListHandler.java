package com.igame.work.checkpoint.handler.worldEvent;





import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.TangSuoTemplate;
import com.igame.core.data.template.WorldEventTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.MyUtil;
import com.igame.work.checkpoint.dto.TangSuoDto;
import com.igame.work.checkpoint.dto.WordEventDto;
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
public class WordEventListHandler extends BaseHandler{
	

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
		
		for(WorldEventTemplate ts : DataManager.WordEventData.getAll()){
			if(ts.getLevel() == 1 &&  MyUtil.hasCheckPoint(player.getCheckPoint(), String.valueOf(ts.getUnlock()))){
				player.getWordEvent().computeIfAbsent(ts.getEvent_type(),wet -> new WordEventDto(player.getPlayerId(), ts.getEvent_type(), "", 0,1));
			}
		}	
		
		vo.addData("world", player.getWordEvent().values());

		send(MProtrol.toStringProtrol(MProtrol.WWORDEVENT_LIST), vo, user);
	}

	
}
