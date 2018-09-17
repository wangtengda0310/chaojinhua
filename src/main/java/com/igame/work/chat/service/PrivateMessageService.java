package com.igame.work.chat.service;

import com.google.common.collect.Lists;
import com.igame.work.chat.dto.Message;
import com.igame.work.user.dto.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.igame.work.chat.MessageContants.*;

/**
 * @author xym
 *
 * 私聊消息服务
 */
public class PrivateMessageService {

    private Map<Long, Map<Long,List<Message>>> privateMessages = new ConcurrentHashMap<>();    //私聊消息

    /**
     * 添加消息缓存
     *      不推送
     * @param senderPlayer 发送者
     * @param recPlayer 接收者
     * @param type 类型 6=世界,7=工会,8=好友私聊,9=陌生人私聊  1-5系统预留  6-10玩家预留
     * @param content 内容
     * @return Message
     */
    public Message addMessage(Player senderPlayer, Player recPlayer, int type, String content) {

        Message message = new Message();

        message.setServerId(senderPlayer.getSeverId());
        message.setType(type);
        message.setSender(senderPlayer.getPlayerId());
        message.setRecipient(recPlayer.getPlayerId());
        message.setContent(content);

        if (type == MSG_TYPE_FRIEND || type == MSG_TYPE_STRANGER){    //好友私聊或者陌生人私聊

            List<Message> messages = getPrivateMessages(senderPlayer).get(recPlayer.getPlayerId());
            if (messages == null){
                messages = Lists.newArrayList();
                messages.add(message);
                getPrivateMessages(senderPlayer).put(recPlayer.getPlayerId(),messages);
            }else {
                if (messages.size() > CACHE_MAX){
                    messages.remove(0);
                }
                messages.add(message);
            }

            List<Message> messages1 = getPrivateMessages(recPlayer).get(senderPlayer.getPlayerId());
            if (messages1 == null){
                messages1 = Lists.newArrayList();
                messages1.add(message);
                getPrivateMessages(recPlayer).put(senderPlayer.getPlayerId(),messages);
            }else {
                if (messages.size() > CACHE_MAX){
                    messages.remove(0);
                }
                messages.add(message);
            }
        }

        return message;
    }

    public Map<Long, List<Message>> getPrivateMessages(Player player) {
        return privateMessages.computeIfAbsent(player.getPlayerId(),pid->new HashMap<>());
    }

    public void setPrivateMessages(Player player, Map<Long, List<Message>> messages) {
        privateMessages.put(player.getPlayerId(), messages);
    }
}
