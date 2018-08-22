package com.igame.work.friend.handler;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.dto.TangSuoDto;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.service.FriendService;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
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
public class FriendExploreHandler extends BaseHandler{

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

		RetVO vo = new RetVO();
		if(reviceMessage(user,params,vo)){
			return;
		}

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }

        List<Map<String,Object>> helpStates = new ArrayList<>();

        List<Friend> curFriends = player.getFriends().getCurFriends();
        for (Friend curFriend : curFriends) {

            long playerId = curFriend.getPlayerId();

            //校验对方角色是否存在
            Player friendPlayer = SessionManager.ins().getSessionByPlayerId(playerId);
            if (friendPlayer == null)//不在线取库
                friendPlayer = PlayerDAO.ins().getPlayerByPlayerId(player.getSeverId(), playerId);

            //获取探索列表
            List<TangSuoDto> exploreList = FriendService.ins().getExploreList(friendPlayer);
            int state = FriendService.ins().getHelpState(exploreList);

            Map<String,Object> helpState = new HashedMap();
            helpState.put("playerId",playerId);
            helpState.put("state",state);
            helpStates.add(helpState);
        }


        vo.addData("exploreStates",helpStates);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE),vo,user);
    }
}
