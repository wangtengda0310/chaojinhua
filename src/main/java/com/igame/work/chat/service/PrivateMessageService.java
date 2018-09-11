package com.igame.work.chat.service;

import com.google.common.collect.Lists;
import com.igame.work.chat.dto.Message;
import com.igame.work.user.dto.Player;

import java.util.List;

import static com.igame.work.chat.MessageContants.*;

/**
 * @author xym
 *
 * 私聊消息服务
 */
public class PrivateMessageService {

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

            List<Message> messages = senderPlayer.getPrivateMessages().get(recPlayer.getPlayerId());
            if (messages == null){
                messages = Lists.newArrayList();
                messages.add(message);
                senderPlayer.getPrivateMessages().put(recPlayer.getPlayerId(),messages);
            }else {
                if (messages.size() > CACHE_MAX){
                    messages.remove(0);
                }
                messages.add(message);
            }

            List<Message> messages1 = recPlayer.getPrivateMessages().get(senderPlayer.getPlayerId());
            if (messages1 == null){
                messages1 = Lists.newArrayList();
                messages1.add(message);
                recPlayer.getPrivateMessages().put(senderPlayer.getPlayerId(),messages);
            }else {
                if (messages.size() > CACHE_MAX){
                    messages.remove(0);
                }
                messages.add(message);
            }
        }

        return message;
    }

}
