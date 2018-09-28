package com.igame.work.chat.service;

import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.core.event.EventService;
import com.igame.core.quartz.TimeListener;
import com.igame.work.chat.dao.MessageDAO;
import com.igame.work.chat.dto.Message;
import com.igame.work.user.dto.Player;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import static com.igame.work.chat.MessageContants.*;

/**
 * @author xym
 *
 * 公聊消息服务
 */
public class PublicMessageService extends EventService implements ISFSModule, TimeListener {
    @Inject private MessageDAO dao;

    @Override
    public void minute5() {
        save();
    }

    //世界频道 <服务器ID,消息列表>
    private static ArrayBlockingQueue<Message> worldMessage;

    //喇叭频道 <服务器ID,消息列表>
    private static ArrayBlockingQueue<Message> hornMessage;

    //工会频道 <工会ID,消息列表>
    private ArrayBlockingQueue<Message> clubMessage;

    //所有删除掉的消息
    private static LinkedList<Message> delMessages = new LinkedList<>();

    /**
     * 添加消息缓存
     *      不推送
     * @param serverId 服务器ID
     * @param type 类型 6=世界,7=工会,8=好友私聊,9=陌生人私聊  1-5系统预留  6-10玩家预留
     * @param sender 发送者
     * @param recipient 接收者
     * @param content 内容
     * @return Message
     */
    public static Message addMessage(int serverId, int type, long sender, long recipient, String content){

        Message message = new Message();

        message.setServerId(serverId);
        message.setType(type);
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(content);

        if (type == MSG_TYPE_WORLD){    //世界

            addQueue(message, worldMessage);

        }else if (type == MSG_TYPE_HORN){    //喇叭

            addQueue(message, hornMessage);

        }else if (type == MSG_TYPE_CLUB){    //工会

            //todo 工会

        }

        return message;
    }

    private static void addQueue(Message message, ArrayBlockingQueue<Message> messages) {

        if (messages == null || message == null)
            return;

        if (messages.size() < com.igame.work.chat.MessageContants.CACHE_MAX){

            messages.add(message);

        }else {

            Message remove = messages.remove();
            if (remove.get_id() != null)
                delMessages.add(remove);

            messages.add(message);
        }
    }

    /**
     * 根据服务器ID获取世界消息
     */
    public static ArrayBlockingQueue<Message> getWorldMessage(){

        return worldMessage;
    }

    /**
     * 根据服务器ID获取世界消息
     */
    public static ArrayBlockingQueue<Message> getHornMessage(){

        return hornMessage;
    }

    /**
     * 初始化
     */
    @Override
    public void init() {
        super.init();

        ArrayBlockingQueue<Message> worldMsg = new ArrayBlockingQueue<>(CACHE_MAX);
        ArrayBlockingQueue<Message> hornMsg = new ArrayBlockingQueue<>(CACHE_MAX);

        List<Message> messages = dao.getMessageByServerId();
        for (Message message : messages) {
            int type = message.getType();

            if (type == MSG_TYPE_WORLD && worldMsg.size() < CACHE_MAX)
                worldMsg.add(message);
            if (type == MSG_TYPE_HORN && hornMsg.size() < CACHE_MAX)
                hornMsg.add(message);
        }

        worldMessage = worldMsg;
        hornMessage = hornMsg;

        //初始化删除数组
        delMessages = new LinkedList<>();
    }

    /**
     * 保存
     */
    public void save(){

        //删除
        delMessages.forEach(message -> dao.delMessage(message));
        delMessages.clear();

        //世界新增
        worldMessage.stream()
                .filter(message -> message.get_id() == null)
                .forEach(message -> dao.saveMessage(message));
        /*for (ArrayBlockingQueue<Message> messages : worldMessage.values()) {
            for (Message message : messages) {
                if (message.get_id() == null)
                    dao.saveMessage(message);
                    //System.out.println("世界新增："+message.getContent());
            }
        }*/

        //喇叭新增
        hornMessage.stream()
                .filter(message -> message.get_id() == null)
                .forEach(message -> dao.saveMessage(message));
        /*for (ArrayBlockingQueue<Message> messages : hornMessage.values()) {
            for (Message message : messages) {
                if (message.get_id() == null)
                    nessageDAO.saveMessage(message);
                    //System.out.println("喇叭新增："+message.getContent());
            }
        }*/

    }

    private Map<Long, Date> lastWorldSpeak = new ConcurrentHashMap<>();//上次世界频道发言
    private Map<Long, Date> lastHornSpeak = new ConcurrentHashMap<>();//上次喇叭频道发言
    private Map<Long, Date> lastClubSpeak = new ConcurrentHashMap<>();//上次工会频道发言

    public Date getLastWorldSpeak(Player player) {
        return lastWorldSpeak.get(player.getPlayerId());
    }

    public void setLastWorldSpeak(Player player, Date date) {
        lastWorldSpeak.put(player.getPlayerId(), date);
    }

    public Date getLastHornSpeak(Player player) {
        return lastHornSpeak.get(player.getPlayerId());
    }

    public void setLastHornSpeak(Player player, Date date) {
        lastHornSpeak.put(player.getPlayerId(), date);
    }

    public Date getLastClubSpeak(Player player) {
        return lastClubSpeak.get(player.getPlayerId());
    }

    public void setLastClubSpeak(Player player, Date date) {
        lastClubSpeak.put(player.getPlayerId(), date);
    }
}
