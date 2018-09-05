package com.igame.work.chat.service;

import com.igame.core.ISFSModule;
import com.igame.core.event.EventService;
import com.igame.core.quartz.TimeListener;
import com.igame.server.GameServerExtension;
import com.igame.work.chat.dao.MessageDAO;
import com.igame.work.chat.dto.Message;
import org.apache.commons.collections.map.HashedMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import static com.igame.work.chat.MessageContants.*;

/**
 * @author xym
 *
 * 公聊消息服务
 */
public class PublicMessageService extends EventService implements ISFSModule, TimeListener {
    @Override
    public void minute5() {
        save();
    }

    //世界频道 <服务器ID,消息列表>
    private static Map<Integer,ArrayBlockingQueue<Message>> worldMessage = new HashedMap();

    //喇叭频道 <服务器ID,消息列表>
    private static Map<Integer,ArrayBlockingQueue<Message>> hornMessage = new HashedMap();

    //工会频道 <工会ID,消息列表>
    private Map<Long,ArrayBlockingQueue<Message>> clubMessage = new HashedMap();

    //所有删除掉的消息
    private static Map<Integer,List<Message>> delMessages = new HashedMap();

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

            addQueue(serverId, message, worldMessage.get(serverId),CACHE_MAX);

        }else if (type == MSG_TYPE_HORN){    //喇叭

            addQueue(serverId, message, hornMessage.get(serverId),CACHE_MAX);

        }else if (type == MSG_TYPE_CLUB){    //工会

            //todo 工会

        }

        return message;
    }

    private static void addQueue(int serverId, Message message, ArrayBlockingQueue<Message> messages, int maxSize) {

        if (messages == null || message == null)
            return;

        if (messages.size() < maxSize){

            messages.add(message);

        }else {

            Message remove = messages.remove();
            if (remove.get_id() != null)
                delMessages.get(serverId).add(remove);

            messages.add(message);
        }
    }

    /**
     * 根据服务器ID获取世界消息
     * @param serverId 服务器ID
     */
    public static ArrayBlockingQueue<Message> getWorldMessage(int serverId){

        return worldMessage.get(serverId);
    }

    /**
     * 根据服务器ID获取世界消息
     * @param serverId 服务器ID
     */
    public static ArrayBlockingQueue<Message> getHornMessage(int serverId){

        return hornMessage.get(serverId);
    }

    /**
     * 初始化
     */
    @Override
    public void init(){

        String DBName = GameServerExtension.dbManager.p.getProperty("DBName");
        String[] DBNames = DBName.split(",");
        for(String db : DBNames){

            int serverId=Integer.parseInt(db.substring(5));
            ArrayBlockingQueue<Message> worldMsg = new ArrayBlockingQueue<>(CACHE_MAX);
            ArrayBlockingQueue<Message> hornMsg = new ArrayBlockingQueue<>(CACHE_MAX);

            List<Message> messages = MessageDAO.ins().getMessageByServerId(serverId);
            for (Message message : messages) {
                int type = message.getType();

                if (type == MSG_TYPE_WORLD && worldMsg.size() < CACHE_MAX)
                    worldMsg.add(message);
                if (type == MSG_TYPE_HORN && hornMsg.size() < CACHE_MAX)
                    hornMsg.add(message);
            }

            worldMessage.put(serverId,worldMsg);
            hornMessage.put(serverId,hornMsg);

            //初始化删除数组
            delMessages.put(serverId,new ArrayList<>());
        }

    }

    /**
     * 保存
     */
    public void save(){

        //删除
        for (List<Message> messages : delMessages.values()) {
            messages.forEach(message -> MessageDAO.ins().delMessage(message));
            messages.clear();
        }

        //世界新增
        worldMessage.values().forEach(
                messages -> messages.forEach(
                        message -> message = message.get_id() == null? MessageDAO.ins().saveMessage(message):message));
        /*for (ArrayBlockingQueue<Message> messages : worldMessage.values()) {
            for (Message message : messages) {
                if (message.get_id() == null)
                    MessageDAO.ins().saveMessage(message);
                    //System.out.println("世界新增："+message.getContent());
            }
        }*/

        //喇叭新增
        hornMessage.values().forEach(
                messages -> messages.forEach(
                        message -> message = message.get_id() == null? MessageDAO.ins().saveMessage(message):message));
        /*for (ArrayBlockingQueue<Message> messages : hornMessage.values()) {
            for (Message message : messages) {
                if (message.get_id() == null)
                    MessageDAO.ins().saveMessage(message);
                    //System.out.println("喇叭新增："+message.getContent());
            }
        }*/

    }

}
