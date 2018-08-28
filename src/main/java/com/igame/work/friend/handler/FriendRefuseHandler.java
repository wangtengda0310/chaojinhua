package com.igame.work.friend.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.service.FriendService;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author xym
 *
 * 拒绝好友请求
 */
public class FriendRefuseHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        long playerId = jsonObject.getLong("playerId");
        vo.addData("playerId",playerId);

        //判断对方是否在自己的请求列表中
        List<Friend> reqFriends = player.getFriends().getReqFriends();
        if (playerId != -1 && reqFriends.stream().noneMatch(req -> req.getPlayerId() == playerId)){
            return error(ErrorCode.ERROR);
        }

        //biz
        List<Long> delReqFriends;   //当前角色删除好友请求列表

        if (playerId != -1){

            FriendService.ins().delReqFriend(player, playerId);
            delReqFriends = Collections.singletonList(playerId);

        }else { //拒绝全部
            delReqFriends = reqFriends.stream().map(Friend::getPlayerId).collect(Collectors.toList());

            reqFriends.clear();
        }

        //推送
        FriendService.ins().pushReqFriends(player,delReqFriends,new ArrayList<>());

        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.FRIEND_REFUSE;
    }

}
