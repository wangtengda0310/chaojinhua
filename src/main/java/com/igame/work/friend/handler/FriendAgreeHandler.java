package com.igame.work.friend.handler;

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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static com.igame.work.friend.FriendConstants.*;

/**
 * @author xym
 *
 * 同意好友请求
 */
public class FriendAgreeHandler extends ReconnectedHandler {

    private static final int max = 20;

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

        //判断对方是否在自己的好友列表中
        List<Friend> curFriends = player.getFriends().getCurFriends();
        if (curFriends.stream().anyMatch(req->req.getPlayerId() == playerId)){
            vo.addData("state",FRIEND_STATE_ADDED);
            return vo;
        }

        //biz
        List<Integer> states;   //返回状态码
        List<Long> delReqFriends = new ArrayList<>();   //当前角色删除好友请求列表
        List<Friend> addFriends = new ArrayList<>();    //当前角色添加好友列表

        if (playerId != -1){

            int state = addFriend(player, delReqFriends, addFriends, playerId);
            states = Collections.singletonList(state);

            fireEvent(player, PlayerEvents.AGREE_FRIEND, Collections.singleton(playerId));
        }else { //同意全部
            states = reqFriends.stream()
                    .map(reqFriend->addFriend(player, delReqFriends, addFriends, reqFriend.getPlayerId()))
                    .collect(Collectors.toList());

            fireEvent(player, PlayerEvents.AGREE_FRIEND, new HashSet<>(reqFriends));
        }

        //推送当前角色好友更新
        FriendService.ins().pushFriends(player,new ArrayList<>(),addFriends);

        //推送当前角色好友请求更新
        FriendService.ins().pushReqFriends(player, delReqFriends,new ArrayList<>());

        vo.addData("states",states);
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.FRIEND_AGREE;
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
        Player reqPlayerCache = PlayerCacheService.getPlayerById(reqPlayerId);

        //校验对方角色是否存在
        if (reqPlayer == null && reqPlayerCache == null){
            return FRIEND_STATE_NOTEXIST;
        }

        //判断自己好友上限
        int myCurFriendCount = player.getFriends().getCurFriends().size();
        if (myCurFriendCount >= max){
            return FRIEND_STATE_MYUP;
        }

        //校验对方角色好友上限
        int curFriendCount;
        if (reqPlayer != null){ //如果对方在线，则取session
            curFriendCount = reqPlayer.getFriends().getCurFriends().size();
        } else {    //如果不在线，则取cache
            curFriendCount = reqPlayerCache.getFriends().getCurFriends().size();
        }

        if (curFriendCount >= max){
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
