package com.igame.work.friend.handler;

import com.igame.work.MProtrol;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.friend.dto.Friend;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
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
public class FriendNominateHandler extends ReconnectedHandler {

    @Override
    protected RetVO handleClientRequest(Player player, ISFSObject params) {

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
                .filter(playerCacheDto->!reqFriends(playerCacheDto).contains(player.getPlayerId()))
                .filter(playerCacheDto->now - playerCacheDto.getLoginoutTime().getTime() <= 24*60*60*1000
                        || Math.abs(playerLevel - playerCacheDto.getPlayerLevel()) <= 10)
                .limit(10)
                .forEach(playerCacheDto->nomFriends.add(new Friend(playerCacheDto)));


        RetVO vo = new RetVO();
        vo.addData("nomFriends",nomFriends);
        return vo;
    }

    @Override
    protected int protocolId() {
        return MProtrol.FRIEND_NOMINATE;
    }

    private Set<Long> reqFriends(Player player) {
        return player.getFriends().getReqFriends().stream()
                .map(Friend::getPlayerId)
                .collect(Collectors.toSet());
    }
}
