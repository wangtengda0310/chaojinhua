package com.igame.work.friend.handler;

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
 * 根据昵称查找好友
 */
public class FriendFindHandler extends ReconnectedHandler {

    private static final int max = 20;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        String nickname = jsonObject.getString("nickname");
        vo.addData("nickname",nickname);

        if (nickname == null || nickname.isEmpty()){
            return error(ErrorCode.PARAMS_INVALID);
        }

        //校验是否为当前用户
        if (player.getNickname().equals(nickname)){
            return error(ErrorCode.PARAMS_INVALID);
        }

        //检验昵称是否存在
        Player reqPlayerCache = PlayerCacheService.ins().getPlayerByNickName(nickname);
        if (reqPlayerCache == null){
            vo.addData("state",FRIEND_STATE_NOTEXIST);
            return vo;
        }

        //校验自己好友上限
        int myCurFriendCount = player.getFriends().getCurFriends().size();
        if (myCurFriendCount >= max){
            vo.addData("state",FRIEND_STATE_MYUP);
            return vo;
        }

        //判断对方好友上限
        Player reqPlayer = SessionManager.ins().getSessionByPlayerId(reqPlayerCache.getPlayerId());
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
        for (Friend curFriend : curFriends) {
            if (curFriend.getNickName().equals(nickname)){
                vo.addData("state",FRIEND_STATE_ADDED);
                return vo;
            }
        }

        //获取对方好友请求列表
        List<Friend> reqFriends;
        if (reqPlayer != null){
            reqFriends = reqPlayer.getFriends().getReqFriends();
        }else {
            FriendInfo friendInfo = FriendDAO.ins().getFriendInfoByPlayerId(player.getSeverId(),reqPlayerCache.getPlayerId());
            reqFriends = friendInfo.getReqFriends();
        }
        //判断自己是否在对方的请求列表中
        for (Friend reqFriend : reqFriends) {
            if (reqFriend.getPlayerId() == player.getPlayerId()){
                vo.addData("state",FRIEND_STATE_REQED);
                return vo;
            }
        }

        //向对方发送好友申请
        FriendService.ins().addReqFriend(player, reqPlayerCache.getPlayerId());

        vo.addData("state",FRIEND_STATE_SUCC);
        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.FRIEND_FIND;
    }

}
