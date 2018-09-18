package com.igame.work.chat.handler;

import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.handler.RetVO;
import com.igame.core.handler.EventDispatcherHandler;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.chat.dto.Message;
import com.igame.work.chat.exception.MessageException;
import com.igame.work.chat.service.PublicMessageService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.exceptions.SFSException;

import java.util.Date;

import static com.igame.work.chat.MessageContants.*;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PublicMessageEventHandler extends EventDispatcherHandler {

	@Inject private ResourceService resourceService;
	@Inject private SessionManager sessionManager;
	@Inject private PublicMessageService publicMessageService;

	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {

		RetVO vo = new RetVO();

		User sender = (User) event.getParameter(SFSEventParam.USER);
		String content = (String) event.getParameter(SFSEventParam.MESSAGE);
		ISFSObject extraParams = (ISFSObject) event.getParameter(SFSEventParam.OBJECT);
		//String infor = extraParams.getUtfString("infor");
		//JSONObject jsonObject = JSONObject.fromObject(infor);

		trace("TestEventHandler-----name :"+sender.getName());
		trace(sender.getName()+" sendClient  message ："+content);
		//trace(sender.getName()+" additionalMsg ："+infor);

		Player player = sessionManager.getSession(Long.parseLong(sender.getName()));
		if(player == null){
			this.getLogger().error("get player failed Name:" +sender.getName());
			return;
		}

		//int type = jsonObject.getInt("type");
		int type = extraParams.getInt("type");
		vo.addData("type",type);

		//校验消息字节
		byte[] buff = content.getBytes();
		if(buff.length > MSG_LENGTH_MAX){
			sendClient(MProtrol.MESSAGE_ERROR,error(ErrorCode.MESSAGE_TOO_LONG),sender);
			throw new MessageException("worldMessage sendClient failed : name="+sender.getName()+",errorCode="+ ErrorCode.MESSAGE_TOO_LONG);
		}

		int recipient = -1;
		if (type == MSG_TYPE_WORLD){	//世界

			//校验间隔时间
			Date lastWorldSpeak = publicMessageService.getLastWorldSpeak(player);
			if (lastWorldSpeak != null && System.currentTimeMillis() - lastWorldSpeak.getTime() < 10000){
				sendClient(MProtrol.MESSAGE_ERROR,error(ErrorCode.SHORT_INTERVAL_TIME),sender);
				throw new MessageException("worldMessage sendClient failed : name="+sender.getName()+",errorCode="+ ErrorCode.SHORT_INTERVAL_TIME);
			}

			publicMessageService.setLastWorldSpeak(player,new Date());

		}else if (type == MSG_TYPE_HORN){	//喇叭

			//校验间隔时间
			Date lastHornSpeak = publicMessageService.getLastHornSpeak(player);
			if (lastHornSpeak != null && System.currentTimeMillis() - lastHornSpeak.getTime() < 20000){
				sendClient(MProtrol.MESSAGE_ERROR,error(ErrorCode.SHORT_INTERVAL_TIME),sender);
				throw new MessageException("hornMessage sendClient failed : name="+sender.getName()+",errorCode="+ ErrorCode.SHORT_INTERVAL_TIME);
			}

			//校验钻石
			int diamond = player.getDiamond();
			if (diamond < 5){
				sendClient(MProtrol.MESSAGE_ERROR,error(ErrorCode.DIAMOND_NOT_ENOUGH),sender);
				throw new MessageException("hornMessage sendClient failed : name="+sender.getName()+",errorCode="+ ErrorCode.DIAMOND_NOT_ENOUGH);
			}

			//扣除钻石
			resourceService.addDiamond(player,-5);

			publicMessageService.setLastHornSpeak(player,new Date());

		}else if (type == MSG_TYPE_CLUB){	//工会

			//校验间隔时间
			Date lastClubSpeak = publicMessageService.getLastClubSpeak(player);
			if (lastClubSpeak != null && System.currentTimeMillis() - lastClubSpeak.getTime() < 3000){
				sendClient(MProtrol.MESSAGE_ERROR,error(ErrorCode.SHORT_INTERVAL_TIME),sender);
				throw new MessageException("clubMessage sendClient failed : name="+sender.getName()+",errorCode="+ ErrorCode.SHORT_INTERVAL_TIME);
			}

			//校验玩家是否加入工会


			publicMessageService.setLastClubSpeak(player,new Date());
		}

		//放入缓存
		Message message = PublicMessageService.addMessage(player.getSeverId(),type,player.getPlayerId(),recipient,content);

		sendClient(MProtrol.MESSAGE_ERROR,vo,sender);
	}

	@Override
	public SFSEventType eventType() {
		return SFSEventType.PUBLIC_MESSAGE;
	}
}