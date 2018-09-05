package com.igame.work.friend.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.friend.dto.Friend;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
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
public class FriendReceivePhyHandler extends ReconnectedHandler {
    private ResourceService resourceService;

    private int state_ungive = 0;   //对方未赠送
    private int state_gave = 1;   //已赠送未领取
    private int state_rec = 2;   //已领取

    private static final int max = 20;

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        long playerId = jsonObject.getLong("playerId");
        vo.addData("playerId",playerId);

        //校验体力领取次数

        long receivedCount = player.getFriends().getCurFriends().stream().filter(friend -> friend.getReceivePhy() == state_rec).count();
        if(receivedCount > max){
            return error(ErrorCode.RECEIVEPHY_NOT_ENOUGH);
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
                return error(ErrorCode.ERROR);
            }
        }

        //biz
        List<HashedMap> voList = new ArrayList<>();   //返回当前体力赠送状态
        if (playerId != -1){

            for (Friend curFriend : curFriends) {
                if (curFriend.getPlayerId() == playerId){
                    int receivePhy = curFriend.getReceivePhy();
                    if (receivePhy != state_gave){   //如果当前状态不等于 已赠送未领取
                        return error(ErrorCode.ERROR);
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
        resourceService.addPhysica(player,(int)count - (int)receivedCount);

        //推送体力领取次数更新
        vo.addData("receiveStates",voList);
        vo.addData("physicalCount",count);
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.FRIEND_PHY_RECEIVE;
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
