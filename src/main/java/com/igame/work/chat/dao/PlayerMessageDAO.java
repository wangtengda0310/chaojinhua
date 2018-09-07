package com.igame.work.chat.dao;

import com.igame.core.db.AbsDao;
import com.igame.work.chat.dto.PlayerMessage;
import com.igame.work.user.dto.Player;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.UpdateOperations;

/**
 * @author xym
 */
public class PlayerMessageDAO extends AbsDao {

    private static final PlayerMessageDAO domain = new PlayerMessageDAO();

    public static PlayerMessageDAO ins() {
        return domain;
    }


    /**
     * 获取某人的私聊记录
     */
    public PlayerMessage getMessageByPlayerId(long playerId) {

        PlayerMessage playerMessage = getDatastore().find(PlayerMessage.class, "playerId", playerId).get();
        if (playerMessage == null){
            playerMessage = new PlayerMessage();
            playerMessage.setPlayerId(playerId);

            saveMessage(playerMessage);
        }

        return playerMessage;
    }

    /**
     * 保存 消息
     * @param message 消息
     */
    public PlayerMessage saveMessage(PlayerMessage message){

        getDatastore().save(message);

        return message;
    }

    /**
     * 更新 消息
     * @param message 消息
     */
    public void updateMessage(PlayerMessage message){

        Datastore ds = getDatastore();
        UpdateOperations<PlayerMessage> up = ds.createUpdateOperations(PlayerMessage.class)
                .set("messages",message.getMessages());

        ds.update(message,up);
    }

    public void updatePlayer(Player player) {

        PlayerMessage playerMessage = getMessageByPlayerId(player.getPlayerId());
        playerMessage.setMessages(player.getPrivateMessages());

        updateMessage(playerMessage);
    }
}
