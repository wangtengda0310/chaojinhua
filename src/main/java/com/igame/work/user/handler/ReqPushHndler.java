package com.igame.work.user.handler;






import java.util.List;
import java.util.stream.Collectors;

import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.MessageUtil;
import com.igame.core.SessionManager;
import com.igame.core.data.DataManager;
import com.igame.core.data.template.ExchangedataTemplate;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.dto.RetVO;
import com.igame.work.quest.service.QuestService;
import com.igame.work.user.dto.MessageCache;
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
public class ReqPushHndler extends BaseHandler{
	

	@Override
	public void handleClientRequest(User user, ISFSObject params) {
		
		String infor = params.getUtfString("infor");
		JSONObject jsonObject = JSONObject.fromObject(infor);
		int index = jsonObject.getInt("index");
		RetVO vo = new RetVO();
		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
			vo.addData("index", index);
			vo.addData("isReLogin", 1);
			send(MProtrol.toStringProtrol(MProtrol.REQ_PUSH), vo, user);
			return;
		}
		player.setUser(user);
		
		if(reviceMessage(user,params,vo)){
			return;
		}

		synchronized (player.getProPushLock()) {
//			MessageCache mc = player.getProTuiMap().get(index);
//			if(mc != null || mc == null){
				List<MessageCache> ls = Lists.newArrayList();
				List<Integer> key = player.getProTuiMap().keySet().stream().collect(Collectors.toList());
        		key.sort((h1, h2) -> h1-h2);
        		for(Integer id : key){
        			if(id > index){
        				ls.add(player.getProTuiMap().get(id));
        			}
        		}
        		for(MessageCache mm : ls){
        			MessageUtil.sendMessageToPlayerNoCache(player, mm);
        		}
//			}
		}
		
		vo.addData("index", index);
		vo.addData("isReLogin", 0);
		send(MProtrol.toStringProtrol(MProtrol.REQ_PUSH), vo, user);
		
	}

	
}
