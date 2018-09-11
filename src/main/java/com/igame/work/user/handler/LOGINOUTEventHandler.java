package com.igame.work.user.handler;

import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.log.ExceptionLog;
import com.igame.core.log.GoldLog;
import com.igame.sfsAdaptor.EventDispatcherHandler;
import com.igame.util.LoginOutReason;
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
public class LOGINOUTEventHandler extends EventDispatcherHandler {


	@Inject private PVPFightService pvpFightService;
	@Inject private SessionManager sessionManager;

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		User user = (User) event.getParameter(SFSEventParam.USER);
		trace("sfs登出事件-----name :"+user.getName());
		Player player =  sessionManager.getSession(Long.parseLong(user.getName()));
		if(player != null){//保存角色数据
			try{
				fireEvent(player, PlayerEvents.OFF_LINE, System.currentTimeMillis());
			}catch(Exception e){
				trace("palyer leave save error----- :",e);
				ExceptionLog.error("palyer leave save error----- :",e);
			}
			
			if(pvpFightService.palyers.containsKey(player.getPlayerId())){
				pvpFightService.chancelFight(player);
			}
			if(pvpFightService.fights.containsKey(player.getPlayerId())){
				pvpFightService.fights.remove(player.getPlayerId());
			}
			player.getFateData().setTodayFateLevel(1);
			player.getFateData().setTodayBoxCount(0);
			player.getFateData().setTempBoxCount(-1);
			player.getFateData().setTempSpecialCount(0);
			player.getFateData().setAddRate(0);
			player.getUser().getZone().removeUser(player.getUser());
			player.getUser().disconnect(new LoginOutReason());
			GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.LOGINOUT,"");
		}
		
		//移除SESSION
		sessionManager.removeSession(Long.parseLong(user.getName()));
		
		
	}

	@Override
	public SFSEventType eventType() {
		return SFSEventType.USER_LOGOUT;
	}
}
