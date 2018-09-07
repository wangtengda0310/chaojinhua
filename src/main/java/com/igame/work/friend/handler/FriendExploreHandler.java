package com.igame.work.friend.handler;

import com.igame.work.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.service.FriendService;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author xym
 *
 * 获取探索状态列表
 */
public class FriendExploreHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        List<Map<String,Object>> helpStates = new ArrayList<>();

        List<Friend> curFriends = player.getFriends().getCurFriends();
        for (Friend curFriend : curFriends) {

            long playerId = curFriend.getPlayerId();

            //校验对方角色是否存在
            Player friendPlayer = SessionManager.ins().getSessionByPlayerId(playerId);
            if (friendPlayer == null)//不在线取库
                friendPlayer = PlayerDAO.ins().getPlayerByPlayerId(playerId);

            //获取探索列表
            List<TansuoDto> exploreList = FriendService.ins().getExploreList(friendPlayer);
            int state = FriendService.ins().getHelpState(exploreList);

            Map<String,Object> helpState = new HashedMap();
            helpState.put("playerId",playerId);
            helpState.put("state",state);
            helpStates.add(helpState);
        }


        vo.addData("exploreStates",helpStates);
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.FRIEND_EXPLORE;
    }

}
