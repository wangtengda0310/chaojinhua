package com.igame.work.friend.handler;

import com.igame.core.di.Inject;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.dto.FriendExplore;
import com.igame.work.friend.service.FriendService;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 获取探索详情
 */
public class FriendGetExploreHandler extends ReconnectedHandler {

    @Inject private FriendService friendService;
    @Inject private PlayerDAO playerDAO;
    @Inject private SessionManager sessionManager;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        long playerId = jsonObject.getLong("playerId");
        vo.addData("playerId",playerId);

        //判断对方是否在自己的好友列表中
        boolean isExist = false;
        List<Friend> curFriends = friendService.getFriends(player).getCurFriends();
        for (Friend curFriend : curFriends) {
            if (curFriend.getPlayerId() == playerId)
                isExist = true;
        }
        if (!isExist){
            return error(ErrorCode.ERROR);
        }

        //校验对方角色是否存在
        Player friendPlayer = sessionManager.getSessionByPlayerId(playerId);
        if (friendPlayer == null)//不在线取库
            friendPlayer = playerDAO.getPlayerByPlayerId(playerId);
        if (friendPlayer == null){
            return error(ErrorCode.ERROR);
        }

        //获取
        List<TansuoDto> exploreList = friendService.getExploreList(friendPlayer);
        int helpState = friendService.getHelpState(exploreList);

        List<FriendExplore> friendExplores = new ArrayList<>();
        for (TansuoDto tansuoDto : exploreList) {
            FriendExplore friendExplore = new FriendExplore(tansuoDto,player.getSeverId());
            friendExplores.add(friendExplore);
        }

        vo.addData("state",helpState);
        vo.addData("tan", friendExplores);
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.FRIEND_EXPLORE_GET;
    }

}
