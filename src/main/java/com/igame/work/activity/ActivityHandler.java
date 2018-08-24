package com.igame.work.activity;

import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.handler.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public abstract class ActivityHandler extends ReconnectedHandler {
    @Override
    public RetVO handleClientRequest(Player player, ISFSObject params) {
        return handleClientRequest(player,player.getActivityData(), params);
    }

    @Override
    protected int protocolId() {
        return activityId();
    }

    protected abstract int activityId();
    public abstract RetVO handleClientRequest(Player player, PlayerActivityData activityData, ISFSObject params);
}
