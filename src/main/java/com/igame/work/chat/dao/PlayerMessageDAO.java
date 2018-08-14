package com.igame.work.chat.dao;

import com.igame.core.db.AbsDao;
import com.igame.work.chat.dto.Message;
import com.igame.work.chat.dto.PlayerMessage;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;

/**
 * @author xym
 */
public class PlayerMessageDAO extends AbsDao {

    @Override
    public String getTableName() {
        return "PlayerMessages";
    }

    private static final PlayerMessageDAO domain = new PlayerMessageDAO();

    public static final PlayerMessageDAO ins() {
        return domain;
    }


    /**
     * 获取某人的私聊记录
     */
    public PlayerMessage getMessageByPlayerId(int serverId, long playerId) {

        PlayerMessage playerMessage = getDatastore(serverId).find(PlayerMessage.class, "playerId", playerId).get();
        if (playerMessage == null){
            playerMessage = new PlayerMessage();
            playerMessage.setPlayerId(playerId);

            saveMessage(serverId,playerMessage);
        }

        return playerMessage;
    }

    /**
     * 保存 消息
     * @param serverId 服务器ID
     * @param message 消息
     */
    public PlayerMessage saveMessage(int serverId, PlayerMessage message){

        getDatastore(serverId).save(message);

        return message;
    }

    /**
     * 更新 消息
     * @param serverId 服务器ID
     * @param message 消息
     */
    public void updateMessage(int serverId, PlayerMessage message){

        Datastore ds = getDatastore(serverId);
        UpdateOperations<PlayerMessage> up = ds.createUpdateOperations(PlayerMessage.class)
                .set("messages",message.getMessages());

        ds.update(message,up);
    }

    public void updatePlayer(Player player) {

        PlayerMessage playerMessage = getMessageByPlayerId(player.getSeverId(), player.getPlayerId());
        playerMessage.setMessages(player.getPrivateMessages());

        updateMessage(player.getSeverId(),playerMessage);
    }
}
