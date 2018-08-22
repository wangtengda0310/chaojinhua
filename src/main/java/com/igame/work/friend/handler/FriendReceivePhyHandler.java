package com.igame.work.friend.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.friend.dto.Friend;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xym
 *
 * 领取体力
 */
public class FriendReceivePhyHandler extends BaseHandler{

    private int state_ungive = 0;   //对方未赠送
    private int state_gave = 1;   //已赠送未领取
    private int state_rec = 2;   //已领取

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

        //校验体力领取次数

        long receivedCount = player.getFriends().getCurFriends().stream().filter(friend -> friend.getReceivePhy() == state_rec).count();
        if(receivedCount > max){
            sendError(ErrorCode.RECEIVEPHY_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.FRIEND_PHY_RECEIVE),vo,user);
            return;
        }

        //判断对方是否在自己的好友列表中
        List<Friend> curFriends = player.getFriends().getCurFriends();
        if (playerId != -1){
            boolean isExist = false;
            for (Friend curFriend : curFriends) {
                if (curFriend.getPlayerId() == playerId) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist){
                sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_PHY_RECEIVE),vo,user);
                return;
            }
        }

        //biz
        List<HashedMap> voList = new ArrayList<>();   //返回当前体力赠送状态
        if (playerId != -1){

            for (Friend curFriend : curFriends) {
                if (curFriend.getPlayerId() == playerId){
                    int receivePhy = curFriend.getReceivePhy();
                    if (receivePhy != state_gave){   //如果当前状态不等于 已赠送未领取
                        sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_PHY_RECEIVE),vo,user);
                        return;
                    }else {
                        voList.add(recPhy(player, curFriend));
                    }
                    break;
                }
            }

        }else {     //全部领取
            curFriends.stream()
                    .filter(friend -> friend.getReceivePhy() == state_gave)
                    .limit(max - receivedCount)
                    .forEach(friend -> voList.add(recPhy(player, friend)));

        }

        long count = player.getFriends().getCurFriends().stream().filter(friend -> friend.getReceivePhy() == state_rec).count();
        //推送体力更新
        ResourceService.ins().addPhysica(player,(int)count - (int)receivedCount);

        //推送体力领取次数更新
        vo.addData("receiveStates",voList);
        vo.addData("physicalCount",count);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_PHY_RECEIVE),vo,user);
    }

    /**
     * 领取体力
     * @param player 当前角色
     * @param curFriend 好友
     * @return voMap 当前角色体力领取状态
     */
    private HashedMap recPhy(Player player, Friend curFriend) {

        //更新状态为已领取
        curFriend.setReceivePhy(state_rec);

        //return
        HashedMap voMap = new HashedMap();
        voMap.put("playerId",curFriend.getPlayerId());
        voMap.put("state",state_rec);
        return voMap;
    }
}
