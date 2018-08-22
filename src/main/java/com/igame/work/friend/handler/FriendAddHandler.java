package com.igame.work.friend.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.friend.dao.FriendDAO;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.dto.FriendInfo;
import com.igame.work.friend.service.FriendService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.PlayerCacheDto;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

import static com.igame.work.friend.FriendConstants.*;

/**
 * @author xym
 *
 * 添加好友
 */
public class FriendAddHandler extends BaseHandler{

    private static final int max = 20;

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

        //校验是否为当前用户
        if (player.getPlayerId() == playerId){
            sendError(ErrorCode.PARAMS_INVALID,MProtrol.toStringProtrol(MProtrol.FRIEND_ADD),vo,user);
            return;
        }

        //判断对方是否存在
        Player reqPlayer = SessionManager.ins().getSessionByPlayerId(playerId);
        PlayerCacheDto reqPlayerCache = PlayerCacheService.ins().getPlayerById(player.getSeverId(), playerId);
        if (reqPlayer == null && reqPlayerCache == null){
            sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_ADD),vo,user);
            return;
        }

        //判断自己好友上限
        int myCurFriendCount = player.getFriends().getCurFriends().size();
        if (myCurFriendCount >= max){
            vo.addData("state",FRIEND_STATE_MYUP);
            sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_ADD),vo,user);
            return;
        }

        //判断对方好友上限
        int curFriendCount;
        if (reqPlayer != null){ //如果对方在线，则取session
            curFriendCount = reqPlayer.getFriends().getCurFriends().size();
        } else {    //如果不在线，则取cache
            curFriendCount = reqPlayerCache.getCurFriendCount();
        }
        if (curFriendCount >= max){
            vo.addData("state",FRIEND_STATE_OTHERUP);
            sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_ADD),vo,user);
            return;
        }

        //判断对方是否在自己的好友列表中
        List<Friend> curFriends = player.getFriends().getCurFriends();
        boolean exist = false;
        for (Friend curFriend : curFriends) {
            if (curFriend.getPlayerId() == playerId)
                exist = true;
        }
        if (exist){
            vo.addData("state",FRIEND_STATE_ADDED);
            sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_ADD),vo,user);
            return;
        }

        //获取对方好友请求列表
        List<Friend> reqFriends;
        if (reqPlayer != null){
            reqFriends = reqPlayer.getFriends().getReqFriends();
        }else {
            FriendInfo friendInfo = FriendDAO.ins().getFriendInfoByPlayerId(player.getSeverId(),playerId);
            reqFriends = friendInfo.getReqFriends();
        }
        //判断自己是否在对方的请求列表中
        for (Friend reqFriend : reqFriends) {
            if (reqFriend.getPlayerId() == player.getPlayerId()){
                vo.addData("state",FRIEND_STATE_REQED);
                sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_ADD),vo,user);
                return;
            }
        }

        //添加好友请求
        FriendService.ins().addReqFriend(player, playerId);

        vo.addData("state",FRIEND_STATE_SUCC);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_ADD),vo,user);
    }
}
