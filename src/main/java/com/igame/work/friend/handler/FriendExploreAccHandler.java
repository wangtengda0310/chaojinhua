package com.igame.work.friend.handler;

import com.igame.core.ErrorCode;
import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.friend.dto.Friend;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author xym
 *
 * 探索加速
 */
public class FriendExploreAccHandler extends BaseHandler{

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

        long playerId = jsonObject.getLong("playerId");
        int exploreMapId = jsonObject.getInt("exploreMapId");
        vo.addData("playerId",playerId);
        vo.addData("exploreMapId",exploreMapId);

        //校验探索可加速次数
        if (helpedFriendsOf(player).count() > 20){
            sendError(ErrorCode.EXPLORE_NOT_ENOUGH,MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE_ACC),vo,user);
            return;
        }

        //判断对方是否在自己的好友列表中
        boolean isExist = false;
        List<Friend> curFriends = player.getFriends().getCurFriends();
        for (Friend curFriend : curFriends) {
            if (curFriend.getPlayerId() == playerId)
                isExist = true;
        }
        if (!isExist){
            sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE_ACC),vo,user);
            return;
        }

        //校验对方角色是否存在
        boolean isOnline = true;
        Player friendPlayer = SessionManager.ins().getSessionByPlayerId(playerId);
        if (friendPlayer == null){  //不在线取库
            isOnline = false;
            friendPlayer = PlayerDAO.ins().getPlayerByPlayerId(player.getSeverId(), playerId);
        }
        if (friendPlayer == null){
            sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE_ACC),vo,user);
            return;
        }

        //异常校验
        TansuoDto tansuoDto = friendPlayer.getTangSuo().get(exploreMapId);
        if (tansuoDto == null){
            sendError(ErrorCode.ERROR,MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE_ACC),vo,user);
            return;
        }

        //校验对方探索是否可加速
        tansuoDto.calLeftTime(System.currentTimeMillis());
        if (tansuoDto.getLeftTime() <= 0 || tansuoDto.getIsHelp() == 1){
            sendError(ErrorCode.EXPLORE_NO_ACC,MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE_ACC),vo,user);
            return;
        }

        //加速
        tansuoDto.help(player);

        //如果对方不在线，则存库
        if (!isOnline){
            PlayerDAO.ins().updatePlayer(friendPlayer,false);
        }

        //减少可加速次数
        player.getFriends().getCurFriends().stream()
                .filter(friend -> friend.getPlayerId() == playerId)
                .forEach(friend -> friend.setHelpAcc(1));

        //推送奖励
        String reward = "1,1,2000";
        int gold = 2000;

        ResourceService.ins().addGold(player,gold);

        //重新计算最新剩余时间
        tansuoDto.calLeftTime(System.currentTimeMillis());

        vo.addData("leftTime", tansuoDto.getLeftTime());
        vo.addData("exploreCount",helpedFriendsOf(player).count());
        vo.addData("reward",reward);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE_ACC),vo,user);
    }

    private Stream<Friend> helpedFriendsOf(Player player) {
        return player.getFriends().getCurFriends().stream().filter(friend -> friend.getHelpAcc() == 1);
    }
}
