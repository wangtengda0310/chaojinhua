package com.igame.work.chat.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseEventHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.chat.dto.Message;
import com.igame.work.chat.exception.MessageException;
import com.igame.work.chat.service.PrivateMessageService;
import com.igame.work.friend.dto.Friend;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.exceptions.SFSException;

import java.util.List;

import static com.igame.work.chat.MessageContants.MSG_LENGTH_MAX;
import static com.igame.work.chat.MessageContants.MSG_TYPE_FRIEND;

/**
 * 
 * @author Marcus.Z
 *
 */
public class PrivateMessageEventHandler extends BaseEventHandler {


	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {

		RetVO vo = new RetVO();
		int type;

		User sender = (User) event.getParameter(SFSEventParam.USER);
		User recipient = (User) event.getParameter(SFSEventParam.RECIPIENT);
		String content = (String) event.getParameter(SFSEventParam.MESSAGE);
		//ISFSObject extraParams = (ISFSObject) event.getParameter(SFSEventParam.OBJECT);
		//String infor = extraParams.getUtfString("infor");
		//JSONObject jsonObject = JSONObject.fromObject(infor);

		Player senderPlayer =  SessionManager.ins().getSession(Long.parseLong(sender.getName()));
		if(senderPlayer == null){
			sendClient(MProtrol.toStringProtrol(MProtrol.MESSAGE_ERROR),error(ErrorCode.ERROR),sender);
			throw new MessageException("senderPlayer not online : name="+sender.getName());
		}

		Player recPlayer =  SessionManager.ins().getSession(Long.parseLong(recipient.getName()));
		if(recPlayer == null){
			sendClient(MProtrol.toStringProtrol(MProtrol.MESSAGE_ERROR),error(ErrorCode.RECIPIENT_NOT_ONLINE),sender);
			throw new MessageException("recipient not online : name="+recipient.getName());
		}

		//判断对方是否在自己的好友列表中
		List<Friend> curFriends = senderPlayer.getFriends().getCurFriends();
		boolean isExist = false;
		for (Friend curFriend : curFriends) {
			if (curFriend.getPlayerId() == recPlayer.getPlayerId())
				isExist = true;
		}

		if (isExist){
			type = MSG_TYPE_FRIEND;
		}else {
			type = MSG_TYPE_FRIEND;
		}

		if (!isExist && recPlayer.getIsBanStrangers() == 0){
			vo.addData("type",type);
			sendClient(MProtrol.toStringProtrol(MProtrol.MESSAGE_ERROR),error(ErrorCode.IS_BAN_STRANGERS),sender);
			throw new MessageException("privateMessage sendClient failed : name="+sender.getName()+",errorCode="+ ErrorCode.MESSAGE_TOO_LONG);
		}

		//校验消息字节
		byte[] buff = content.getBytes();
		if(buff.length > MSG_LENGTH_MAX){
			vo.addData("type",type);
			sendClient(MProtrol.toStringProtrol(MProtrol.MESSAGE_ERROR),error(ErrorCode.MESSAGE_TOO_LONG),sender);
			throw new MessageException("privateMessage sendClient failed : name="+sender.getName()+",errorCode="+ ErrorCode.MESSAGE_TOO_LONG);
		}

		//放入缓存
		Message message = PrivateMessageService.ins().addMessage(senderPlayer,recPlayer,type,content);
		sendClient(MProtrol.toStringProtrol(MProtrol.MESSAGE_ERROR),vo,sender);
	}
	
}
