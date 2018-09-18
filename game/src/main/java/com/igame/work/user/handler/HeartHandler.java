package com.igame.work.user.handler;


import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.handler.ClientDispatcherHandler;
import com.igame.core.handler.RetVO;
import com.igame.util.DateUtil;
import com.igame.work.MProtrol;
import com.igame.work.user.dto.Player;
import com.igame.work.user.service.PlayerCacheService;
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
public class HeartHandler extends ClientDispatcherHandler {
    private Map<Long, Integer> timer = new ConcurrentHashMap<>();
    @Inject private SessionManager sessionManager;
    @Inject private PlayerCacheService playerCacheService;

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        RetVO vo = new RetVO();

        Player player = sessionManager.getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }
        playerCacheService.setHeartTime(player.getPlayerId(), System.currentTimeMillis());
        int times = timer.computeIfAbsent(player.getPlayerId(), k->0);
        if (times++ > 10) {
            times = 0;
            vo.addData("dateTime", DateUtil.formatClientDateTime(new Date()));
        }
        timer.put(player.getPlayerId(), times);
        sendClient(MProtrol.HEART,vo,user);
    }

    @Override
    public int protocolId() {
        return MProtrol.HEART;
    }
}
