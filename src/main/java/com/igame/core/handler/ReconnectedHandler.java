package com.igame.core.handler;

import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

public abstract class ReconnectedHandler extends BaseHandler implements GameHandler {

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        if (reviceMessage(user, params, new RetVO())) {
            return;
        }

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }

        RetVO vo = handleClientRequest(player, params);

        sendSucceed(MProtrol.toStringProtrol(protocolId()), vo, user);
    }

    protected abstract int protocolId();
    /** 不是用这个方法则需要实现断线重连的消息重发 */
    protected abstract RetVO handleClientRequest(Player player, ISFSObject params);
}
