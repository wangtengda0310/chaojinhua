package com.igame.core.handler;

import java.util.List;
import java.util.stream.Collectors;

import net.sf.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.core.SessionManager;
import com.igame.dto.RetVO;
import com.igame.work.user.dto.MessageCache;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

/**
 * @author xym
 */
public abstract class BaseHandler extends BaseClientRequestHandler{
	
	
	protected boolean reviceMessage(User user, ISFSObject params,RetVO vo) {
		
		Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player == null){
			this.getLogger().error("get player failed Name:" +user.getName());
			return false;
		}
		int index = params.getInt("index");
		MessageCache res = player.getProMap().get(index);
		if(res != null){
			send(res.getCmdName(), res.getiSFSObject(), user);
			return true;
		}
		vo.setIndex(index);
		
		return false;
	}

    protected void sendError(int errorCode, String cmdName, RetVO vo, User user) {

        vo.setState(1);
        vo.setErrCode(errorCode);

        send(cmdName,vo, user);
    }

    protected void sendSucceed(String cmdName, RetVO vo, User user) {

        vo.setState(0);

        send(cmdName,vo, user);
    }

    protected void send(String cmdName, RetVO vo, User user) {
        ObjectMapper mapper = new ObjectMapper();
        String json = null;
        ISFSObject res = new SFSObject();

        try {
            json = mapper.writeValueAsString(vo);
        } catch (JsonProcessingException e) {
            this.getLogger().warn(cmdName+"  Vo2Json error", e);
        }

        res.putUtfString("infor", json);
        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player != null){
	        synchronized (player.getProLock()) {
	        	player.getProMap().put(vo.getIndex(), new MessageCache(cmdName, res));
	        	int size = player.getProMap().size();
	        	if(size > 10){
	        		int left = size - 10;
	        		List<Integer> key = player.getProMap().keySet().stream().collect(Collectors.toList());
	        		key.sort((h1, h2) -> h1-h2);
	        		if(key.size() < left){
	        			left = key.size();
	        		}
	        		for(int i = 0;i < left;i++){
	        			player.getProMap().remove(key.get(i));
	        		}
	        		
	        	}
			}
		}
//		if(!"500".equals(cmdName)){
			  send(cmdName, res, user);
//		}
      
    }

}
