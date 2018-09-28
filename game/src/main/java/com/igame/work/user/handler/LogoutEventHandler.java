package com.igame.work.user.handler;

import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.handler.EventDispatcherHandler;
import com.igame.util.BanDisconnectionReason;
import com.igame.work.PlayerEvents;
import com.igame.work.fight.service.PVPFightService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;

/**
 * 
 * @author Marcus.Z
 *
 */
public class LogoutEventHandler extends EventDispatcherHandler {

	@Inject private SessionManager sessionManager;

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		User user = (User) event.getParameter(SFSEventParam.USER);
		trace("\nsfs登出事件-----name :"+user.getName()+"\n");
		Player player =  sessionManager.getSession(Long.parseLong(user.getName()));
		if(player != null){//保存角色数据
			fireEvent(player, PlayerEvents.OFF_LINE, System.currentTimeMillis());

            player.getUser().disconnect(new BanDisconnectionReason());  // logout后需要disconnect吗
		}
		
	}

	@Override
	public SFSEventType eventType() {
		return SFSEventType.USER_LOGOUT;
	}
}
