package com.igame.core.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.core.SessionManager;
import com.igame.work.user.dto.MessageCache;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xym
 */
public abstract class BaseHandler extends BaseClientRequestHandler implements GameHandler {
	private static Map<String, EventListener> listeners = new HashMap<>();

	{
		if(eventListener()!=null) {
			listeners.put(getClass().getSimpleName(), eventListener());
		}
	}

	/** 接受用户相关的事件并预处理后传给GameHandler.EventListener */
	public static BaseServerEventHandler serverEventListener() {
		return new BaseServerEventHandler() {
			@Override
			public void handleServerEvent(ISFSEvent isfsEvent) {
				if (isfsEvent.getType()== SFSEventType.USER_VARIABLES_UPDATE && isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP)!=null) {
					if(isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP) instanceof Map) {
						Map parameters = (Map) isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP);
						Object parameter = parameters.get("last.event");
						if(parameter instanceof SFSUserVariable) {
							for (EventListener listener: listeners.values()) {
								listener.handleEvent(((SFSUserVariable)parameter).getValue());
							}
						}
					}
				}
			}
		};
	}

	/** 发送用户相关的事件
	 * 使用的是setUserVariables 所以会受到用户变量限制 SmartFoxServer默认允许每个玩家使用5个变量
	 */
	public void fireEvent(User user, Object event) {

		getApi().setUserVariables(user, Collections.singletonList(new SFSUserVariable("last.event",event, false, true)), false, true);
	}

	// TODO move to ReconnectedHandler
	/** 断线重连的消息重发 */
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

    public void send(String cmdName, RetVO vo, User user) {
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
