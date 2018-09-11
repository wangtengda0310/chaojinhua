package com.igame.work.chat.dao;

import com.igame.core.db.AbsDao;
import com.igame.work.chat.dto.MessageBoard;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.List;

/**
 * @author xym
 */
public class MessageBoardDAO extends AbsDao {

    /**
     * 根据 服务器ID 获取 消息
     * @param type 留言板类型
     */
    public List<MessageBoard> getMessageBoard(String type){
        Datastore ds = getDatastore();
        Query<MessageBoard> q = ds.createQuery(MessageBoard.class)
                .field("type").equal(type);

        return q.asList();
    }


    /**
     * 根据 服务器ID 获取 消息
     * @param id 留言板ID
     */
    public MessageBoard getMessageBoardById(String id){
        Datastore ds = getDatastore();
        Query<MessageBoard> q = ds.createQuery(MessageBoard.class)
                .field("_id").equal(new ObjectId(id));

        return q.get();
    }

    /**
     * 保存 消息
     * @param message 消息
     */
    public MessageBoard saveMessageBoard(MessageBoard message){
        getDatastore().save(message);
        return message;
    }

    /**
     * 删除 消息
     * @param message 消息
     */
    public void delMessageBoard(MessageBoard message){


    }

    /**
     * 更新 消息
     * @param message 消息
     */
    public void updateMessageBoard(MessageBoard message){

        Datastore ds = getDatastore();
        UpdateOperations<MessageBoard> up = ds.createUpdateOperations(MessageBoard.class)
                .set("like",message.getLike())
                .set("dislike",message.getDislike());

        ds.update(message,up);
    }
}
