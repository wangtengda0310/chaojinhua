package com.igame.work.friend.handler;

import com.igame.core.di.Inject;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.PlayerEvents;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.service.FriendService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 删除好友
 */
public class FriendDeleteHandler extends ReconnectedHandler {

    @Inject private FriendService friendService;
    @Inject private SessionManager sessionManager;
    @Inject private PlayerCacheService playerCacheService;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        long playerId = jsonObject.getLong("playerId");
        vo.addData("playerId",playerId);

        //判断对方是否在自己的好友列表中
        List<Friend> curFriends = friendService.getFriends(player).getCurFriends();
        if (curFriends.stream().noneMatch(friend -> friend.getPlayerId() == playerId)){
            return error(ErrorCode.ERROR);
        }

        //校验要删除的好友
        Player delPlayer = sessionManager.getSessionByPlayerId(playerId);
        Player delPlayerCache = playerCacheService.getPlayerById(playerId);
        if (delPlayer == null && delPlayerCache == null){
            return error(ErrorCode.ERROR);
        }

        fireEvent(player, PlayerEvents.DELETE_FRIEND, playerId);
        //删除好友
        friendService.delFriend(player,playerId);

        //推送当前角色好友更新
        List<Long> delFriends = new ArrayList<>();
        delFriends.add(playerId);
        friendService.pushFriends(player,delFriends,new ArrayList<>());

        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.FRIEND_DELETE;
    }

}
