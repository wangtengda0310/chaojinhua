package com.igame.work.user.handler;


import com.igame.core.SessionManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.DateUtil;
import com.igame.work.MProtrol;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Marcus.Z
 *
 */
public class HeartHandler extends BaseHandler{
    private Map<Long, Integer> timer = new ConcurrentHashMap<>();

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        RetVO vo = new RetVO();

        Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }
        player.setHeartTime(System.currentTimeMillis());
        int times = timer.computeIfAbsent(player.getPlayerId(), k->0);
        if (times++ > 10) {
            times = 0;
            vo.addData("dateTime", DateUtil.formatClientDateTime(new Date()));
        }
        timer.put(player.getPlayerId(), times);
        sendClient(MProtrol.HEART,vo,user);
    }
}
