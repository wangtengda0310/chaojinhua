package com.igame.work.chat.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
import com.igame.core.db.BasicDto;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Transient;

import java.util.Date;
import java.util.List;

/**
 * @author xym
 */
@Entity(value = "MessageBoard", noClassnameStored = true)
public class MessageBoard extends BasicDto implements Comparable<MessageBoard>{

    @Transient
    private String objectId;

    @JsonIgnore
    private String type;   //星核之眼(ID=层数)、冒险关卡(ID=关卡ID)、世界事件(ID*100+难度)、龙眠海峡(ID?)、怪物图鉴(ID=怪兽ID)

    private long playerId;   //留言人ID

    private String content; //留言内容

    @JsonIgnore
    private Date time; //留言时间

    @Transient
    private int likeCount; //点赞人数

    @Transient
    private int isLike; //当前角色是否点赞  0=未点赞，1=已点赞

    @Transient
    private int dislikeCount; //反对人数

    @Transient
    private int isDislike; //当前角色是否反对  0=未反对，1=已反对

    @JsonIgnore
    private List<Long> like = Lists.newArrayList();    //点赞详情

    @JsonIgnore
    private List<Long> dislike = Lists.newArrayList(); //反对详情

    @JsonIgnore
    private long userId;

    @Transient
    private String name = "";

    @Transient
    private int playerFrameId;//玩家头像框

    @Transient
    private int playerHeadId;//玩家头像

    @Transient
    private int playerLv;//玩家等级

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
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

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public int getIsDislike() {
        return isDislike;
    }

    public void setIsDislike(int isDislike) {
        this.isDislike = isDislike;
    }

    public List<Long> getLike() {
        return like;
    }

    public void setLike(List<Long> like) {
        this.like = like;
    }

    public List<Long> getDislike() {
        return dislike;
    }

    public void setDislike(List<Long> dislike) {
        this.dislike = dislike;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPlayerFrameId() {
        return playerFrameId;
    }

    public void setPlayerFrameId(int playerFrameId) {
        this.playerFrameId = playerFrameId;
    }

    public int getPlayerHeadId() {
        return playerHeadId;
    }

    public void setPlayerHeadId(int playerHeadId) {
        this.playerHeadId = playerHeadId;
    }

    public int getPlayerLv() {
        return playerLv;
    }

    public void setPlayerLv(int playerLv) {
        this.playerLv = playerLv;
    }

    @Override
    public int compareTo(MessageBoard o) {
        if (o == null || !(o instanceof MessageBoard)) {
            return 0;
        }
        if (this.time.getTime() > o.getTime().getTime()) {
            return 1;
        } else if (this.time.getTime() < o.getTime().getTime()) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "MessageBoard{" +
                "objectId='" + objectId + '\'' +
                ", likeCount=" + likeCount +
                '}';
    }
}
