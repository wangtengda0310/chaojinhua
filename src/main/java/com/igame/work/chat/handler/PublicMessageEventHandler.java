package com.igame.work.chat.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseEventHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.chat.dto.Message;
import com.igame.work.chat.exception.MessageException;
import com.igame.work.chat.service.PublicMessageService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
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
public class PublicMessageEventHandler extends BaseEventHandler {


	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {

		RetVO vo = new RetVO();

		User sender = (User) event.getParameter(SFSEventParam.USER);
		String content = (String) event.getParameter(SFSEventParam.MESSAGE);
		ISFSObject extraParams = (ISFSObject) event.getParameter(SFSEventParam.OBJECT);
		//String infor = extraParams.getUtfString("infor");
		//JSONObject jsonObject = JSONObject.fromObject(infor);

		trace("TestEventHandler-----name :"+sender.getName());
		trace(sender.getName()+" send  message ："+content);
		//trace(sender.getName()+" additionalMsg ："+infor);

		Player player = SessionManager.ins().getSession(Long.parseLong(sender.getName()));
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
			sendError(ErrorCode.MESSAGE_TOO_LONG, MProtrol.toStringProtrol(MProtrol.MESSAGE_ERROR),vo,sender);
			throw new MessageException("worldMessage send failed : name="+sender.getName()+",errorCode="+ ErrorCode.MESSAGE_TOO_LONG);
		}

		int recipient = -1;
		if (type == MSG_TYPE_WORLD){	//世界

			//校验间隔时间
			Date lastWorldSpeak = player.getLastWorldSpeak();
			if (lastWorldSpeak != null && System.currentTimeMillis() - lastWorldSpeak.getTime() < 10000){
				sendError(ErrorCode.SHORT_INTERVAL_TIME, MProtrol.toStringProtrol(MProtrol.MESSAGE_ERROR),vo,sender);
				throw new MessageException("worldMessage send failed : name="+sender.getName()+",errorCode="+ ErrorCode.SHORT_INTERVAL_TIME);
			}

			player.setLastWorldSpeak(new Date());

		}else if (type == MSG_TYPE_HORN){	//喇叭

			//校验间隔时间
			Date lastHornSpeak = player.getLastHornSpeak();
			if (lastHornSpeak != null && System.currentTimeMillis() - lastHornSpeak.getTime() < 20000){
				sendError(ErrorCode.SHORT_INTERVAL_TIME, MProtrol.toStringProtrol(MProtrol.MESSAGE_ERROR),vo,sender);
				throw new MessageException("hornMessage send failed : name="+sender.getName()+",errorCode="+ ErrorCode.SHORT_INTERVAL_TIME);
			}

			//校验钻石
			int diamond = player.getDiamond();
			if (diamond < 5){
				sendError(ErrorCode.DIAMOND_NOT_ENOUGH, MProtrol.toStringProtrol(MProtrol.MESSAGE_ERROR),vo,sender);
				throw new MessageException("hornMessage send failed : name="+sender.getName()+",errorCode="+ ErrorCode.DIAMOND_NOT_ENOUGH);
			}

			//扣除钻石
			ResourceService.ins().addDiamond(player,-5);

			player.setLastHornSpeak(new Date());

		}else if (type == MSG_TYPE_CLUB){	//工会

			//校验间隔时间
			Date lastClubSpeak = player.getLastClubSpeak();
			if (lastClubSpeak != null && System.currentTimeMillis() - lastClubSpeak.getTime() < 3000){
				sendError(ErrorCode.SHORT_INTERVAL_TIME, MProtrol.toStringProtrol(MProtrol.MESSAGE_ERROR),vo,sender);
				throw new MessageException("clubMessage send failed : name="+sender.getName()+",errorCode="+ ErrorCode.SHORT_INTERVAL_TIME);
			}

			//校验玩家是否加入工会


			player.setLastClubSpeak(new Date());
		}

		//放入缓存
		Message message = PublicMessageService.ins().addMessage(player.getSeverId(),type,player.getPlayerId(),recipient,content);

		sendSucceed(MProtrol.toStringProtrol(MProtrol.MESSAGE_ERROR),vo,sender);
	}
	
}
