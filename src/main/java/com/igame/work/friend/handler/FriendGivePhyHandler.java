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
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author xym
 *
 * 赠送好友体力
 */
public class FriendGivePhyHandler extends ReconnectedHandler {

    private int state_unGive = 0;   //未赠送
    private int state_gave = 1;   //已赠送

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        long playerId = jsonObject.getInt("playerId");
        vo.addData("playerId",playerId);

        //判断对方是否在自己的好友列表中
        List<Friend> curFriends = player.getFriends().getCurFriends();
        if (playerId != -1){
            boolean isExist = false;
            for (Friend curFriend : curFriends) {
                if (curFriend.getPlayerId() == playerId)
                    isExist = true;
            }
            if (!isExist){
                return error(ErrorCode.ERROR);
            }
        }

        //biz
        List<HashedMap> voList = new ArrayList<>();   //返回当前体力赠送状态
        if (playerId != -1){

            for (Friend curFriend : curFriends) {
                if (curFriend.getPlayerId() == playerId){
                    if (curFriend.getGivePhy() != state_unGive){   //如果当前状态不等于 未赠送
                        return error(ErrorCode.ERROR);
                    }else {
                        givePhy(player, curFriend, voList);
                    }
                }
            }

        }else {    //全部赠送

            for (Friend curFriend : curFriends) {
                if (curFriend.getGivePhy() == state_unGive){   //如果当前状态等于 未赠送
                    givePhy(player, curFriend, voList);
                }
            }

        }

        vo.addData("giveStates",voList);
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.FRIEND_PHY_GIVE;
    }

    /**
     * 赠送体力
     * @param sendPlayer 谁要赠送
     * @param recFriend 赠送给谁
     * @param voList 当前角色体力赠送状态
     */
    private void givePhy(Player sendPlayer, Friend recFriend, List<HashedMap> voList) {

        int recState = 1;   //已赠送未领取

        //更新当前角色好友体力赠送状态
        recFriend.setGivePhy(state_gave);

        //返回
        HashedMap voMap = new HashedMap();
        voMap.put("playerId",recFriend.getPlayerId());
        voMap.put("state",state_gave);
        voList.add(voMap);

        //更新对方体力领取状态
        long recPlayerId = recFriend.getPlayerId();
        Player recPlayer = SessionManager.ins().getSessionByPlayerId(recPlayerId);
        if (recPlayer != null){    //如果对方在线

            List<Friend> curFriends = recPlayer.getFriends().getCurFriends();
            for (Friend friend : curFriends) {
                if (friend.getPlayerId() == sendPlayer.getPlayerId())
                    friend.setReceivePhy(recState);   //已赠送未领取
            }

            //推送
            FriendService.ins().pushFriendPhy(recPlayer,sendPlayer.getPlayerId());

        }else { //存库

            FriendInfo friendInfo = FriendDAO.ins().getFriendInfoByPlayerId(recPlayerId);
            List<Friend> curFriends = friendInfo.getCurFriends();
            for (Friend friend : curFriends) {
                if (friend.getPlayerId() == sendPlayer.getPlayerId()){
                    friend.setGivePhyDate(new Date());  //记录赠送时间
                    friend.setReceivePhy(recState);   //已赠送未领取
                }
            }

            FriendDAO.ins().updateFriends(friendInfo);
        }

    }
}
