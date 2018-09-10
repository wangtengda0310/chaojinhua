package com.igame.work.user.handler;

import com.igame.core.SessionManager;
import com.igame.sfsAdaptor.EventDispatcherHandler;
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
public class DisconnectEventHandler extends EventDispatcherHandler {


	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		User user = (User) event.getParameter(SFSEventParam.USER);
		trace("DisconnectEventHandler-----userId :"+user.getName());
		Player player =  SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player != null){//保存角色数据
//			if(player.getHeartTime()>0 && System.currentTimeMillis() - player.getHeartTime() > 5 * 60 * 1000){
//				try{
//					fireEvent(player, PlayerEvents.OFF_LINE, System.currentTimeMillis());
//				}catch(Exception e){
//					trace("palyer leave save error----- :",e);
//					ExceptionLog.error("palyer leave save error----- :",e);
//				}
//				
//				if(PVPFightService.ins().palyers.containsKey(player.getPlayerId())){
//					PVPFightService.ins().chancelFight(player);
//				}
//				if(PVPFightService.ins().fights.containsKey(player.getPlayerId())){
//					PVPFightService.ins().fights.remove(player.getPlayerId());
//				}
//				player.getFateData().setTodayFateLevel(1);
//				player.getFateData().setTodayBoxCount(0);
//				player.getFateData().setTempBoxCount(-1);
//				player.getFateData().setTempSpecialCount(0);
//				player.getFateData().setAddRate(0);
//				PlayerCacheService.cachePlayer(player);
//				player.getUser().getZone().removeUser(player.getUser());
//				player.getUser().disconnect(new LoginOutReason());
//				GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.RELOGIN_NOT_ALLOWED,"");
//				SessionManager.ins().removeSession(Long.parseLong(user.getName()));
//			}

		}
		
	}

	@Override
	public SFSEventType eventType() {
		return SFSEventType.USER_DISCONNECT;
	}
}
