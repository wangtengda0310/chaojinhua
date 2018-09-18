package com.igame.work.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.igame.core.db.BasicDto;
import org.mongodb.morphia.annotations.Entity;

import java.util.Date;

/**
 * @author xym
 *
 * 公聊消息
 */
@Entity(value = "Messages", noClassnameStored = true)
public class Message extends BasicDto {

    @JsonIgnore
    private int serverId;//服务器ID

    //private int exttype;//类型  1-系统，2-玩家

    @JsonIgnore
    private int type;//类型 6=世界,7=喇叭,8=工会,9=好友私聊,10=陌生人私聊  1-5系统预留

    private long sender;//发送者

    @JsonIgnore
    private long recipient;//接收者    -1等于全部  如果类型等于工会,接收者等于工会ID

    private String content=""; //内容

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss", locale = "zh", timezone = "GMT+8")
    private Date time = new Date(); //发送时间

    @JsonIgnore
    private String attach=""; //附件

    public Message() {

    }

    public Message(Message message) {
        this.serverId = message.getServerId();
        this.type = message.getType();
        this.sender = message.getSender();
        this.recipient = message.getRecipient();
        this.content = message.getContent();
        this.time = message.getTime();
        this.attach = message.getAttach();
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getSender() {
        return sender;
    }

    public void setSender(long sender) {
        this.sender = sender;
    }

    public long getRecipient() {
        return recipient;
    }

    public void setRecipient(long recipient) {
        this.recipient = recipient;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }
}
