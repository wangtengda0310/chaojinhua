package com.igame.work.friend.dao;

import com.igame.core.db.AbsDao;
import com.igame.work.friend.dto.FriendInfo;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

public class FriendDAO extends AbsDao {

    private static final FriendDAO domain = new FriendDAO();

    public static FriendDAO ins() {
        return domain;
    }


    /**
     * 根据 玩家ID 获取 玩家好友信息
     * @param playerId 玩家ID
     */
    public FriendInfo getFriendInfoByPlayerId(long playerId){

        FriendInfo friendInfo = getDatastore().find(FriendInfo.class, "playerId", playerId).get();

        if (friendInfo != null){
            friendInfo.getCurFriends().forEach(friend -> friend.loadCache(friend));
            friendInfo.getReqFriends().forEach(friend -> friend.loadCache(friend));
        }else {

            friendInfo = new FriendInfo(playerId);

            saveFriendInfo(friendInfo);
        }

        return friendInfo;
    }

    /**
     * 初始化 玩家好友信息
     * @param friendInfo 好友信息
     */
    public FriendInfo saveFriendInfo(FriendInfo friendInfo){

        getDatastore().save(friendInfo);

        return friendInfo;
    }

    /**
     * 更新 玩家好友信息
     * @param friendInfo 好友信息
     */
    public void updateFriends(FriendInfo friendInfo){

        Datastore ds = getDatastore();
        UpdateOperations<FriendInfo> up = ds.createUpdateOperations(FriendInfo.class)
                .set("curFriends",friendInfo.getCurFriends())
                .set("reqFriends",friendInfo.getReqFriends());

        ds.update(friendInfo,up);
    }

    public void updatePlayer(Player player) {

        if(player.getFriends().get_id() == null){
            saveFriendInfo(player.getFriends());
        }else{
            updateFriends(player.getFriends());
        }

    }

}
