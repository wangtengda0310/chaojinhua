package com.igame.work.user.handler;

import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.ExceptionLog;
import com.igame.core.log.GoldLog;
import com.igame.util.KickIDisconnectionReason;
import com.igame.work.fight.service.PVPFightService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.PlayerLoad;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class LoginOutHandler extends BaseHandler {


	@Override
	public void handleClientRequest(User user, ISFSObject params) {

		trace("LoginOutHandlerLoginOutHandler-----name :"+user.getName());
		Player player =  SessionManager.ins().getSession(Long.parseLong(user.getName()));
		if(player != null){//保存角色数据
			try{
				PlayerLoad.ins().savePlayer(player,true);
			}catch(Exception e){
				trace("palyer leave save error----- :",e);
				ExceptionLog.error("palyer leave save error----- :",e);
			}
			
			if(PVPFightService.ins().palyers.containsKey(player.getPlayerId())){
				PVPFightService.ins().chancelFight(player);
			}
			if(PVPFightService.ins().fights.containsKey(player.getPlayerId())){
				PVPFightService.ins().fights.remove(player.getPlayerId());
			}
			player.getFateData().setTodayFateLevel(1);
			player.getFateData().setTodayBoxCount(0);
			player.getFateData().setTempBoxCount(-1);
			player.getFateData().setTempSpecialCount(0);
			player.getFateData().setAddRate(0);
			player.getUser().getZone().removeUser(player.getUser());
			player.getUser().disconnect(new KickIDisconnectionReason());
			GoldLog.info(player.getSeverId(), player.getUserId(), player.getPlayerId(), GoldLog.LOGINOUT,"");
		}
		
		//移除SESSION
		SessionManager.ins().removeSession(Long.parseLong(user.getName()));
	}
	
}
