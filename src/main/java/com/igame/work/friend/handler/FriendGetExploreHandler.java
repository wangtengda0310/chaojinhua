package com.igame.work.friend.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.checkpoint.dto.TangSuoDto;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.dto.FriendExplore;
import com.igame.work.friend.service.FriendService;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 获取探索详情
 */
public class FriendGetExploreHandler extends BaseHandler{

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
            sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE_GET),vo,user);
            return;
        }

        //校验对方角色是否存在
        Player friendPlayer = SessionManager.ins().getSessionByPlayerId(playerId);
        if (friendPlayer == null)//不在线取库
            friendPlayer = PlayerDAO.ins().getPlayerByPlayerId(player.getSeverId(), playerId);
        if (friendPlayer == null){
            sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE_GET),vo,user);
            return;
        }

        //获取
        List<TangSuoDto> exploreList = FriendService.ins().getExploreList(friendPlayer);
        int helpState = FriendService.ins().getHelpState(exploreList);

        List<FriendExplore> friendExplores = new ArrayList<>();
        for (TangSuoDto tangSuoDto : exploreList) {
            FriendExplore friendExplore = new FriendExplore(tangSuoDto,player.getSeverId());
            friendExplores.add(friendExplore);
        }

        vo.addData("state",helpState);
        vo.addData("tan", friendExplores);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE_GET),vo,user);
    }
}
