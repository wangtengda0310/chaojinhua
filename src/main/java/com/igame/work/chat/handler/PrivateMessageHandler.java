package com.igame.work.chat.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.chat.dto.Message;
import com.igame.work.chat.dto.PlayerInfo;
import com.igame.work.chat.dto.PlayerMessage;
import com.igame.work.chat.service.PublicMessageService;
import com.igame.work.friend.dto.Friend;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 获取私聊消息
 */
public class PrivateMessageHandler extends BaseHandler{

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        Player senderPlayer = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if(senderPlayer == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }

        //判断对方是否在线
        long playerId = jsonObject.getLong("playerId");
        Player recPlayer = SessionManager.ins().getSessionByPlayerId(playerId);
        if (recPlayer == null){
            sendError(ErrorCode.RECIPIENT_NOT_ONLINE,MProtrol.toStringProtrol(MProtrol.MESSAGE_PRIVATE),vo,user);
            return;
        }

        //判断对方是否在自己的好友列表中
        List<Friend> curFriends = senderPlayer.getFriends().getCurFriends();
        boolean isExist = false;
        for (Friend curFriend : curFriends) {
            if (curFriend.getPlayerId() == recPlayer.getPlayerId())
                isExist = true;
        }

        if (!isExist && recPlayer.getIsBanStrangers() == 0){
            sendError(ErrorCode.IS_BAN_STRANGERS, MProtrol.toStringProtrol(MProtrol.MESSAGE_PRIVATE),vo,user);
            return;
        }

        List<Message> messages = senderPlayer.getPrivateMessages().get(playerId);

        vo.addData("senderCache",new PlayerInfo(recPlayer));
        vo.addData("privateMsg", messages);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.MESSAGE_PRIVATE),vo,user);
    }
}
