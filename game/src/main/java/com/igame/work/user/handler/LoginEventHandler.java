package com.igame.work.user.handler;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.handler.CachedMessages;
import com.igame.core.handler.EventDispatcherHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.KickIDisconnectionReason;
import com.igame.work.MProtrol;
import com.igame.work.PlayerEvents;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;

/**
 * 
 * @author Marcus.Z
 *
 */
public class LoginEventHandler extends EventDispatcherHandler {
	@Inject private SessionManager sessionManager;
	@Inject private PlayerCacheService playerCacheService;

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		
		String name = (String) event.getParameter(SFSEventParam.LOGIN_NAME); 
		User user = (User) event.getParameter(SFSEventParam.USER);
		ISFSObject isRepeat  = (ISFSObject)event.getParameter(SFSEventParam.LOGIN_IN_DATA);

		trace("\nLoginEventHandler-----userId :"+name + ",isRepeat :" + isRepeat.getBool("isRepeat")+"\n");

		Player player = sessionManager.getSession(Long.parseLong(name));
		if(player != null && !isRepeat.getBool("isRepeat")){
			fireEvent(player, PlayerEvents.OFF_LINE, System.currentTimeMillis());	// 断线重连这不用异步消息出去保存数据吧 直接切换个session?
			RetVO vo = new RetVO();
			ObjectMapper mapper = new ObjectMapper();
			String json = null;
			ISFSObject res = new SFSObject();
			try {
				json = mapper.writeValueAsString(vo);
			} catch (JsonProcessingException e) {
				this.getLogger().warn("LoginEventHandler  error", e);
			}	
			res.putUtfString("infor", json);
			send(MProtrol.toStringProtrol(MProtrol.RELOGIN_NOT_ALLOWED), res, player.getUser());
			player.getUser().getZone().removeUser(player.getUser());
			player.getUser().disconnect(new KickIDisconnectionReason());
			sessionManager.removeSession(Long.parseLong(name));
		}

		
		//是重连但是玩家不存在
		if(player != null){
			playerCacheService.setHeartTime(player.getPlayerId(), System.currentTimeMillis());
//			RetVO vo = new RetVO();
//			ObjectMapper mapper = new ObjectMapper();
//			String json = null;
//			ISFSObject res = new SFSObject();
//			try {
//				json = mapper.writeValueAsString(vo);
//			} catch (JsonProcessingException e) {
//				this.getLogger().warn("LoginEventHandler  error", e);
//			}	
//			res.putUtfString("infor", json);
//			sendClient(MProtrol.toStringProtrol(MProtrol.RELOGIN_NOT_ALLOWED), res, player.getUser());

		}
//		ISFSObject params = (ISFSObject) event.getParameter(SFSEventParam.LOGIN_IN_DATA);
		

//		String token = params.getUtfString("token");
//		String uid = params.getUtfString("uid");

		boolean result = true;
		if (!result){
//			trace("-----uid :"+uid);
//			trace("-----url :"+url);s
//			trace("token :"+token);
		     // 创建要发送给客户端的错误代码
		     SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
		     errData.addParameter(name);
		     
		     // 抛出登录异常
		     throw new SFSLoginException("vertify failed uid:", errData);
		}
		
	}

	@Override
	public SFSEventType eventType() {
		return SFSEventType.USER_LOGIN;
	}
}
