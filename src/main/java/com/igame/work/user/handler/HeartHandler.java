package com.igame.work.user.handler;


import com.igame.core.MProtrol;
import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.dto.RetVO;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

/**
 * 
 * @author Marcus.Z
 *
 */
public class HeartHandler extends BaseHandler{

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        RetVO vo = new RetVO();

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }
        player.setHeartTime(System.currentTimeMillis());
        sendSucceed(MProtrol.toStringProtrol(MProtrol.HEART),vo,user);
    }
}
