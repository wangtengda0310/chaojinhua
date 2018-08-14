package com.igame.work.friend.handler;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.util.GameMath;
import com.igame.work.friend.dto.Friend;
import com.igame.work.user.dto.Player;
import com.igame.work.user.dto.PlayerCacheDto;
import com.igame.work.user.service.PlayerCacheService;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
        List<Friend> curFriends = player.getFriends().getCurFriends();
        List<Friend> nomFriends = new ArrayList<>();


        List<PlayerCacheDto> players = PlayerCacheService.ins().getPlayers(player.getSeverId());
        List<PlayerCacheDto> one = new ArrayList<>();   //第一优先级
        List<PlayerCacheDto> two = new ArrayList<>();   //第二优先级
        List<PlayerCacheDto> three = new ArrayList<>();   //第三优先级

        for (PlayerCacheDto playerCacheDto : players) {

            //如果是当前玩家,跳出本次循环
            if (playerCacheDto.getPlayerId() == player.getPlayerId())
                continue;

            //如果是当前玩家的好友，跳出本次循环
            boolean isFriend = false;
            for (Friend curFriend : curFriends) {
                if (playerCacheDto.getPlayerId() == curFriend.getPlayerId()){
                    isFriend = true;
                    break;
                }
            }
            if (isFriend)
                continue;

            Date lastLoginOutDate = playerCacheDto.getLastLoginOutDate();

            int playerLevel1 = playerCacheDto.getPlayerLevel();
            if (new Date().getTime() - lastLoginOutDate.getTime() <= 24*60*60*1000){
                if (Math.abs(playerLevel - playerLevel1) <= 10){
                    one.add(playerCacheDto);
                }
                two.add(playerCacheDto);
            }
            three.add(playerCacheDto);
        }

        //随机十个
        if (one.size() > 10){
            //随机并添加新的好友
            random(nomFriends, one);
        }/*else if (two.size() > 10){
            //随机并添加新的好友
            random(nomFriends, two);

        }else if (three.size() > 10){
            //随机并添加新的好友
            random(nomFriends, three);
        }*/else {
            for (PlayerCacheDto cacheDto : three) {
                nomFriends.add(new Friend(cacheDto));
            }
        }

        vo.addData("nomFriends",nomFriends);
        sendSucceed(MProtrol.toStringProtrol(MProtrol.FRIEND_NOMINATE),vo,user);
    }

    private void random(List<Friend> nomFriends, List<PlayerCacheDto> pool) {

        ok:
        while (true){
            int i = new Random().nextInt(pool.size());
            PlayerCacheDto playerCacheDto = pool.get(i);
            nomFriends.add(new Friend(playerCacheDto));
            pool.remove(i);
            if (nomFriends.size() >= 10)
                break ok;
        }
    }
}
