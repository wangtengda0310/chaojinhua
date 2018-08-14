package com.igame.work.chat.dto;

import com.igame.work.user.dto.PlayerCacheDto;
import com.igame.work.user.service.PlayerCacheService;

/**
 * @author xym
 *
 * 公聊消息vo
 */
public class PublicMessageVo extends Message {

    private long userId;

    private String name = "";

    private int playerFrameId;//玩家头像框

    private int playerHeadId;//玩家头像

    public PublicMessageVo() {
    }

    public PublicMessageVo(Message message) {

        super(message);
        PlayerCacheDto cacheDto = PlayerCacheService.ins().getPlayerById(message.getServerId(), message.getSender());
        this.userId = cacheDto.getUserId();
        this.name = cacheDto.getName();
        this.playerFrameId = cacheDto.getPlayerFrameId();
        this.playerHeadId = cacheDto.getPlayerHeadId();

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
}
