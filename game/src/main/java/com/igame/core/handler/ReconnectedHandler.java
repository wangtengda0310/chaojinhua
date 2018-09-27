package com.igame.core.handler;

import com.igame.core.SessionManager;
import com.igame.core.di.Inject;
import com.igame.core.event.RemoveOnLogout;
import com.igame.work.ErrorCode;
import com.igame.work.MProtrol;
import com.igame.work.user.dto.MessageCache;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public abstract class ReconnectedHandler extends ClientDispatcherHandler {

    @Inject private SessionManager sessionManager;
    @Inject private CachedMessages cachedMessages;

    /** 不使用这个方法则需要实现断线重连的消息重发 */
    protected abstract RetVO handleClientRequest(Player player, ISFSObject params) throws Exception;

    @Override
    public void handleClientRequest(User user, ISFSObject params) {

        if (cachedResponse(user, params)) {
            return;
        }

        Player player = sessionManager.getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error(this.getClass().getSimpleName()," get player failed Name:" +user.getName());
            return;
        }

        RetVO vo;
        try {
            vo = handleClientRequest(player, params);
        } catch (Exception e) {
            warn("",e);
            vo = error(ErrorCode.ERROR);
        }

        vo.setIndex(params.getInt("index"));
        String cmdName = MProtrol.toStringProtrol(protocolId());
        ISFSObject res = sendClient(cmdName, vo, user);

        cacheResponse(cmdName, vo, user, res);

    }

    @RemoveOnLogout() private Map<Long, Object> proLock = new ConcurrentHashMap<>();//消息同步锁

    private void cacheResponse(String cmdName, RetVO vo, User user, ISFSObject res) {
        Player player = sessionManager.getSession(Long.parseLong(user.getName()));
        if(player != null){
            synchronized (proLock.computeIfAbsent(player.getPlayerId(), pid->new Object())) {
                Map<Integer, MessageCache> proMsg = cachedMessages.getProMsg(player);
                proMsg.put(vo.getIndex(), new MessageCache(cmdName, res));
                int size = proMsg.size();
                if(size > 10){
                    int left = size - 10;
                    List<Integer> key = proMsg.keySet().stream()
                            .sorted(Comparator.comparingInt(h -> h))
                            .collect(Collectors.toList());
                    if(key.size() < left){
                        left = key.size();
                    }
                    for(int i = 0;i < left;i++){
                        proMsg.remove(key.get(i));
                    }

                }
            }
        }
    }

    /** 断线重连后的消息重发 */
    protected boolean cachedResponse(User user, ISFSObject params) {
        // 这个方法好像是因为SmartFoxServer有超时强制断线的机制 以前的服务端跟客户端协商出来的解决办法

        Player player = sessionManager.getSession(Long.parseLong(user.getName()));
        if(player == null){
            this.getLogger().error("get player failed Name:" +user.getName());
            return false;
        }
        int index = params.getInt("index");
        MessageCache res = cachedMessages.getProMsg(player).get(index);
        if(res != null){
            send(res.getCmdName(), res.getiSFSObject(), user);
            return true;
        }

        return false;
    }

}
