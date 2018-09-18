package com.igame.work.chat.dto;

import com.igame.work.user.dto.Player;

/**
 * @author xym
 *
 * 公聊消息vo
 */
public class PublicMessageDto extends Message {

    private long userId;

    private String name = "";

    private int playerFrameId;//玩家头像框

    private int playerHeadId;//玩家头像

    public PublicMessageDto() {
    }

    public PublicMessageDto(Message message,Player cacheDto) {

        super(message);
        this.userId = cacheDto.getUserId();
        this.name = cacheDto.getNickname();
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
