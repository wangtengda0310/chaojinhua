package com.igame.work.friend.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.service.FriendService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 拒绝好友请求
 */
public class FriendRefuseHandler extends BaseHandler{

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }

        long playerId = jsonObject.getLong("playerId");
        vo.addData("playerId",playerId);

        //判断对方是否在自己的请求列表中
        List<Friend> reqFriends = player.getFriends().getReqFriends();
        if (playerId != -1){
            boolean exist = false;
            for (Friend reqFriend : reqFriends) {
                if (reqFriend.getPlayerId() == playerId)
                    exist = true;
            }

            if (!exist){
                sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_REFUSE),vo,user);
                return;
            }
        }

        //biz
        List<Long> delReqFriends = new ArrayList<>();   //当前角色删除好友请求列表

        if (playerId != -1){

            FriendService.ins().delReqFriend(player, playerId);
            delReqFriends.add(playerId);

        }else { //拒绝全部

            for (Friend reqFriend : reqFriends) {
                long delPlayerId = reqFriend.getPlayerId();
                delReqFriends.add(delPlayerId);
            }

            reqFriends.clear();
        }

        //推送
        FriendService.ins().pushReqFriends(player,delReqFriends,new ArrayList<>());

        sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_REFUSE),vo,user);
    }
}
