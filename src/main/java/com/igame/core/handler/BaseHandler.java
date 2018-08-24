package com.igame.core.handler;

import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
	protected void fireEvent(Player player, Object event) {

		getApi().setUserVariables(player.getUser(), Collections.singletonList(new SFSUserVariable("last.event",event, false, true)), false, true);
	}

	@Override
	public void warn(String warn, Exception e) {

		this.getLogger().warn(warn, e);
	}
	@Override
	public void sendClient(User user, String cmdName, ISFSObject params) {
		send(cmdName, params, user);
	}
}
