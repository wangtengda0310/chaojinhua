package com.igame.work.friend.handler;

import com.igame.core.di.Inject;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.friend.dao.FriendDAO;
import com.igame.work.friend.dto.Friend;
import com.igame.work.friend.dto.FriendInfo;
import com.igame.work.friend.service.FriendService;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;

import static com.igame.work.friend.FriendConstants.*;

/**
 * @author xym
 *
 * 添加好友
 */
public class FriendAddHandler extends ReconnectedHandler {

    private static final int max = 20;
    @Inject private FriendDAO dao;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        long playerId = jsonObject.getLong("playerId");
        vo.addData("playerId",playerId);

        //校验是否为当前用户
        if (player.getPlayerId() == playerId){
            return error(ErrorCode.PARAMS_INVALID);
        }

        //判断对方是否存在
        Player reqPlayer = SessionManager.ins().getSessionByPlayerId(playerId);
        Player reqPlayerCache = PlayerCacheService.getPlayerById(playerId);
        if (reqPlayer == null && reqPlayerCache == null){
            return error(ErrorCode.ERROR);
        }

        //判断自己好友上限
        int myCurFriendCount = player.getFriends().getCurFriends().size();
        if (myCurFriendCount >= max){
            vo.addData("state",FRIEND_STATE_MYUP);
            return vo;
        }

        //判断对方好友上限
        int curFriendCount;
        if (reqPlayer != null){ //如果对方在线，则取session
            curFriendCount = reqPlayer.getFriends().getCurFriends().size();
        } else {    //如果不在线，则取cache
            curFriendCount = reqPlayerCache.getFriends().getCurFriends().size();
        }
        if (curFriendCount >= max){
            vo.addData("state",FRIEND_STATE_OTHERUP);
            return vo;
        }

        //判断对方是否在自己的好友列表中
        List<Friend> curFriends = player.getFriends().getCurFriends();
        if (curFriends.stream().anyMatch(friend -> friend.getPlayerId() == playerId)){
            vo.addData("state",FRIEND_STATE_ADDED);
            return vo;
        }

        //获取对方好友请求列表
        List<Friend> reqFriends;
        if (reqPlayer != null){
            reqFriends = reqPlayer.getFriends().getReqFriends();
        }else {
            FriendInfo friendInfo = dao.getFriendInfoByPlayerId(playerId);
            reqFriends = friendInfo.getReqFriends();
        }
        //判断自己是否在对方的请求列表中
        if (reqFriends.stream().anyMatch(friend -> friend.getPlayerId() == player.getPlayerId())){
            vo.addData("state",FRIEND_STATE_REQED);
            return vo;
        }

        //添加好友请求
        FriendService.ins().addReqFriend(player, playerId);

        vo.addData("state",FRIEND_STATE_SUCC);
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.FRIEND_ADD;
    }

}
