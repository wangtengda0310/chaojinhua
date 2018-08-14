package com.igame.work.friend.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.service.FriendService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.PlayerCacheDto;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 删除好友
 */
public class FriendDeleteHandler extends BaseHandler{

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

        //判断对方是否在自己的好友列表中
        boolean isExist = false;
        List<Friend> curFriends = player.getFriends().getCurFriends();
        for (Friend curFriend : curFriends) {
            if (curFriend.getPlayerId() == playerId)
                isExist = true;
        }
        if (!isExist){
            sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_DELETE),vo,user);
            return;
        }

        //校验要删除的好友
        Player delPlayer = SessionManager.ins().getSessionByPlayerId(playerId);
        PlayerCacheDto delPlayerCache = PlayerCacheService.ins().getPlayerById(player.getSeverId(), playerId);
        if (delPlayer == null && delPlayerCache == null){
            sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_DELETE),vo,user);
            return;
        }

        //删除好友
        FriendService.ins().delFriend(player,playerId);

        //推送当前角色好友更新
        List<Long> delFriends = new ArrayList<>();
        delFriends.add(playerId);
        FriendService.ins().pushFriends(player,delFriends,new ArrayList<>());

        sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_DELETE),vo,user);
    }
}
