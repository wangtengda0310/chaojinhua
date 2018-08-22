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
import com.smartfoxserver.v2.entities.User;
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
public class FriendGivePhyHandler extends BaseHandler{

    private int state_unGive = 0;   //未赠送
    private int state_gave = 1;   //已赠送

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
                sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_PHY_GIVE),vo,user);
                return;
            }
        }

        //biz
        List<HashedMap> voList = new ArrayList<>();   //返回当前体力赠送状态
        if (playerId != -1){

            for (Friend curFriend : curFriends) {
                if (curFriend.getPlayerId() == playerId){
                    if (curFriend.getGivePhy() != state_unGive){   //如果当前状态不等于 未赠送
                        sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_PHY_GIVE),vo,user);
                        return;
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
        sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_PHY_GIVE),vo,user);
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

            FriendInfo friendInfo = FriendDAO.ins().getFriendInfoByPlayerId(sendPlayer.getSeverId(), recPlayerId);
            List<Friend> curFriends = friendInfo.getCurFriends();
            for (Friend friend : curFriends) {
                if (friend.getPlayerId() == sendPlayer.getPlayerId()){
                    friend.setGivePhyDate(new Date());  //记录赠送时间
                    friend.setReceivePhy(recState);   //已赠送未领取
                }
            }

            FriendDAO.ins().updateFriends(sendPlayer.getSeverId(), friendInfo);
        }

    }
}
