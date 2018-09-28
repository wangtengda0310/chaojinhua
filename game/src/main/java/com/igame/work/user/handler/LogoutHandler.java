package com.igame.work.user.handler;

import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.handler.ClientDispatcherHandler;
import com.igame.core.log.ExceptionLog;
import com.igame.core.log.GoldLog;
import com.igame.util.KickIDisconnectionReason;
import com.igame.work.MProtrol;
import com.igame.work.PlayerEvents;
import com.igame.work.fight.service.PVPFightService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * 
 * @author Marcus.Z
 *
 */
public class LogoutHandler extends ClientDispatcherHandler {


	@Inject private PVPFightService pvpFightService;
	@Inject private SessionManager sessionManager;

	@Override
	public void handleClientRequest(User user, ISFSObject params) {

		trace("\nLoginOutHandlerLoginOutHandler-----name :"+user.getName()+"\n");
		Player player =  sessionManager.getSession(Long.parseLong(user.getName()));
		if(player != null){//保存角色数据
			fireEvent(player, PlayerEvents.OFF_LINE, System.currentTimeMillis());
		}
	}

	@Override
	public int protocolId() {
		return MProtrol.LOGOUT;
	}
}
