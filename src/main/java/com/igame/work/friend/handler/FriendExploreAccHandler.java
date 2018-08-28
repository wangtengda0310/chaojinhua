package com.igame.work.friend.handler;

import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.checkpoint.tansuo.TansuoDto;
import com.igame.work.friend.dto.Friend;
import com.igame.work.user.dao.PlayerDAO;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import net.sf.json.JSONObject;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author xym
 *
 * 探索加速
 */
public class FriendExploreAccHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

		RetVO vo = new RetVO();

        String infor = params.getUtfString("infor");
        JSONObject jsonObject = JSONObject.fromObject(infor);

        long playerId = jsonObject.getLong("playerId");
        int exploreMapId = jsonObject.getInt("exploreMapId");
        vo.addData("playerId",playerId);
        vo.addData("exploreMapId",exploreMapId);

        //校验探索可加速次数
        if (helpedFriendsOf(player).count() > 20){
            return error(ErrorCode.EXPLORE_NOT_ENOUGH);
        }

        //判断对方是否在自己的好友列表中
        boolean isExist = false;
        List<Friend> curFriends = player.getFriends().getCurFriends();
        for (Friend curFriend : curFriends) {
            if (curFriend.getPlayerId() == playerId)
                isExist = true;
        }
        if (!isExist){
            return error(ErrorCode.ERROR);
        }

        //校验对方角色是否存在
        boolean isOnline = true;
        Player friendPlayer = SessionManager.ins().getSessionByPlayerId(playerId);
        if (friendPlayer == null){  //不在线取库
            isOnline = false;
            friendPlayer = PlayerDAO.ins().getPlayerByPlayerId(player.getSeverId(), playerId);
        }
        if (friendPlayer == null){
            return error(ErrorCode.ERROR);
        }

        //异常校验
        TansuoDto tansuoDto = friendPlayer.getTangSuo().get(exploreMapId);
        if (tansuoDto == null){
            return error(ErrorCode.ERROR);
        }

        //校验对方探索是否可加速
        tansuoDto.calLeftTime(System.currentTimeMillis());
        if (tansuoDto.getLeftTime() <= 0 || tansuoDto.getIsHelp() == 1){
            return error(ErrorCode.EXPLORE_NO_ACC);
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
        return vo;
    }

    @Override
    public int protocolId() {
        return MProtrol.FRIEND_EXPLORE_ACC;
    }

    private Stream<Friend> helpedFriendsOf(Player player) {
        return player.getFriends().getCurFriends().stream().filter(friend -> friend.getHelpAcc() == 1);
    }
}
