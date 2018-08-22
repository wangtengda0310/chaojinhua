package com.igame.work.chat.dto;

import com.google.common.collect.Maps;
import com.igame.core.db.BasicDto;
import org.mongodb.morphia.annotations.Entity;

import java.util.List;
import java.util.Map;

/**
 * @author xym
 *
 * 私聊消息
 */
@Entity(value = "PlayerMessages", noClassnameStored = true)
public class PlayerMessage extends BasicDto {

    private long playerId;  //角色ID

    private Map<Long,List<Message>> messages = Maps.newHashMap();   //消息 <对方角色ID,消息列表>

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public Map<Long, List<Message>> getMessages() {
        return messages;
    }

    public void setMessages(Map<Long, List<Message>> messages) {
        this.messages = messages;
    }
}
