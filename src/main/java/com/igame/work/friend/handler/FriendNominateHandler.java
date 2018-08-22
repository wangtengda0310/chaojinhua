package com.igame.work.friend.handler;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.friend.dto.Friend;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author xym
 *
 * 好友推荐
 *
 * 离线24小时以内，等级差10级以内的优先抽取推荐
 */
public class FriendNominateHandler extends BaseHandler{

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


        //搜索好友的优先级：离线24小时以内，等级差10级以内的优先抽取推荐
        int playerLevel = player.getPlayerLevel();
        Set<Long> curFriends = player.getFriends().getCurFriends().stream()
                .map(Friend::getPlayerId)
                .collect(Collectors.toSet());
        List<Friend> nomFriends = new ArrayList<>();


        List<Player> players = PlayerCacheService.ins().getPlayers(player.getSeverId());

        long now = new Date().getTime();
        players.stream()
                .filter(playerCacheDto->playerCacheDto.getPlayerId() != player.getPlayerId())
                .filter(playerCacheDto->!curFriends.contains(playerCacheDto.getPlayerId()))
                .filter(playerCacheDto->now - playerCacheDto.getLoginoutTime().getTime() <= 24*60*60*1000
                        || Math.abs(playerLevel - playerCacheDto.getPlayerLevel()) <= 10)
                .limit(10)
                .forEach(playerCacheDto->nomFriends.add(new Friend(playerCacheDto)));

        vo.addData("nomFriends",nomFriends);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_NOMINATE),vo,user);
    }

}
