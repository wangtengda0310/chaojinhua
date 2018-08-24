package com.igame.work.chat.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.chat.dto.Message;
import com.igame.work.chat.dto.PlayerInfo;
import com.igame.work.friend.dto.Friend;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * @author xym
 *
 * 获取私聊消息
 */
public class PrivateMessageHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {
		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        //判断对方是否在线
        long playerId = jsonObject.getLong("playerId");
        Player recPlayer = SessionManager.ins().getSessionByPlayerId(playerId);
        if (recPlayer == null){
            return error(ErrorCode.RECIPIENT_NOT_ONLINE);
        }

        //判断对方是否在自己的好友列表中
        List<Friend> curFriends = player.getFriends().getCurFriends();
        boolean isExist = false;
        for (Friend curFriend : curFriends) {
            if (curFriend.getPlayerId() == recPlayer.getPlayerId())
                isExist = true;
        }

        if (!isExist && recPlayer.getIsBanStrangers() == 0){
            return error(ErrorCode.IS_BAN_STRANGERS);
        }

        List<Message> messages = player.getPrivateMessages().get(playerId);

        vo.addData("senderCache",new PlayerInfo(recPlayer));
        vo.addData("privateMsg", messages);
        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.MESSAGE_PRIVATE;
    }

}
