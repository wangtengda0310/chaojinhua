package com.igame.work.chat.dao;

import com.igame.core.db.AbsDao;
import com.igame.work.chat.dto.Message;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;

/**
 * @author xym
 */
public class MessageDAO extends AbsDao {

    private static final MessageDAO domain = new MessageDAO();

    public static MessageDAO ins() {
        return domain;
    }


    /**
     * 根据 服务器ID 获取 消息
     */
    public List<Message> getMessageByServerId(){

        return getDatastore().find(Message.class).asList();
    }

    /**
     * 获取俩人的聊天记录
     */
    public List<Message> getMessageByPlayerId(long sender) {

        Datastore ds = getDatastore();
        Query<Message> q = ds.createQuery(Message.class);
        q.field("sender").equal(sender);
        q.or(q.criteria("recipient").equal(sender));

        return q.asList();
    }

    /**
     * 获取俩人的聊天记录

    public List<Message> getMessageByPlayerId(int serverId, long sender, long recipient) {

        Datastore ds = getAccountDatastore(serverId);
        Query<Message> q = ds.createQuery(Message.class).field("sender").equal(sender)
                .field("recipient").equal(recipient);

        return q.asList();
    }*/

    /**
     * 保存 消息
     * @param message 消息
     */
    public Message saveMessage(Message message){

        getDatastore().save(message);

        return message;
    }

    /**
     * 删除 消息
     * @param message 消息
     */
    public void delMessage(Message message){

        getDatastore().delete(message);

    }

    /**
     * 更新 消息
     * @param serverId 服务器ID
     * @param message 消息
     */
    public void updateMessage(Message message){

        Datastore ds = getDatastore();
        UpdateOperations<Message> up = ds.createUpdateOperations(Message.class);

        ds.update(message,up);
    }
}
