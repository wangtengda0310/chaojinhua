package com.igame.core.handler;

import com.igame.core.event.RemoveOnLogout;
import com.igame.work.user.dto.MessageCache;
import com.igame.work.user.dto.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CachedMessages {
    @RemoveOnLogout() private Map<Long, Map<Integer, MessageCache>> proMap = new ConcurrentHashMap<>();//消息缓存

    public Map<Integer, MessageCache> getProMsg(Player player) {
        return proMap.computeIfAbsent(player.getPlayerId(), pik -> new HashMap<>());
    }

    public void removeProMsg(Player player) {
        proMap.remove(player.getPlayerId());
    }
}
