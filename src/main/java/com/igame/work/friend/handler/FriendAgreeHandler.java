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

import static com.igame.work.friend.FriendConstants.*;

/**
 * @author xym
 *
 * 同意好友请求
 */
public class FriendAgreeHandler extends BaseHandler{

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

        //判断对方是否在自己的请求列表中
        List<Friend> reqFriends = player.getFriends().getReqFriends();
        if (playerId != -1){
            boolean exist = false;
            for (Friend reqFriend : reqFriends) {
                if (reqFriend.getPlayerId() == playerId)
                    exist = true;
            }

            if (!exist){
                sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_AGREE),vo,user);
                return;
            }
        }

        //判断对方是否在自己的好友列表中
        List<Friend> curFriends = player.getFriends().getCurFriends();
        for (Friend curFriend : curFriends) {
            if (curFriend.getPlayerId() == playerId){
                vo.addData("state",FRIEND_STATE_ADDED);
                sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_AGREE),vo,user);
                return;
            }
        }

        //biz
        List<Integer> states = new ArrayList<>();   //返回状态码
        List<Long> delReqFriends = new ArrayList<>();   //当前角色删除好友请求列表
        List<Friend> addFriends = new ArrayList<>();    //当前角色添加好友列表

        if (playerId != -1){

            int state = addFriend(player, delReqFriends, addFriends, playerId);
            states.add(state);

        }else { //同意全部

            for (int i = 0; i < reqFriends.size(); i++) {

                Friend reqFriend = reqFriends.get(i);
                int state = addFriend(player, delReqFriends, addFriends, reqFriend.getPlayerId());
                states.add(state);

            }
        }

        //推送当前角色好友更新
        FriendService.ins().pushFriends(player,new ArrayList<>(),addFriends);

        //推送当前角色好友请求更新
        FriendService.ins().pushReqFriends(player, delReqFriends,new ArrayList<>());

        vo.addData("states",states);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_AGREE),vo,user);
    }

    /**
     * 同意好友请求
     * @param player 当前角色
     * @param delReqFriends 删除的请求列表
     * @param addFriends 添加的好友列表
     * @param reqPlayerId 好友ID
     * @return 状态码
     */
    private int addFriend(Player player, List<Long> delReqFriends, List<Friend> addFriends, long reqPlayerId) {

        Player reqPlayer = SessionManager.ins().getSessionByPlayerId(reqPlayerId);
        PlayerCacheDto reqPlayerCache = PlayerCacheService.ins().getPlayerById(player.getSeverId(), reqPlayerId);

        //校验对方角色是否存在
        if (reqPlayer == null && reqPlayerCache == null){
            return FRIEND_STATE_NOTEXIST;
        }

        //判断自己好友上限
        int myCurFriendCount = player.getFriends().getCurFriends().size();
        int myMaxFriendCount = player.getPlayerTop().getFriendCount();
        if (myCurFriendCount >= myMaxFriendCount){
            return FRIEND_STATE_MYUP;
        }

        //校验对方角色好友上限
        int curFriendCount;
        int maxFriendCount;
        if (reqPlayer != null){ //如果对方在线，则取session
            curFriendCount = reqPlayer.getFriends().getCurFriends().size();
            maxFriendCount = reqPlayer.getPlayerTop().getFriendCount();
        } else {    //如果不在线，则取cache
            curFriendCount = reqPlayerCache.getCurFriendCount();
            maxFriendCount = reqPlayerCache.getMaxFriendCount();
        }

        if (curFriendCount >= maxFriendCount){
            return FRIEND_STATE_OTHERUP;
        }

        //添加好友
        FriendService.ins().addFriend(player,reqPlayerId);

        if (reqPlayer != null) {
            addFriends.add(new Friend(reqPlayer));
        } else {
            addFriends.add(new Friend(reqPlayerCache));
        }

        //删除好友请求
        FriendService.ins().delReqFriend(player,reqPlayerId);

        delReqFriends.add(reqPlayerId);

        return FRIEND_STATE_SUCC;
    }
}
