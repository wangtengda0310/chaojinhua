package com.igame.work.checkpoint.worldEvent;

import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WorldEventService implements ISFSModule {
    @Inject
    private ResourceService resourceService;

    private Map<Long, String> enterWordEventId = new ConcurrentHashMap<>();//进入世界事件关卡ID

    public String getString(Player player, String monsterExpStr, WorldEventTemplate wt, List<Monster> ll, long mid) {
        if(-1 != mid){
            Monster mm = player.getMonsters().get(mid);
            if(mm != null){
                int mmExp = CheckPointService.getTotalExp(mm, wt.getPhysical() * 5);
                monsterExpStr += mid;
                if(resourceService.addMonsterExp(player, mid, mmExp, false) == 0){
                    ll.add(mm);
                    monsterExpStr += ("," + mmExp +";");
                }else{
                    monsterExpStr += ",0;";
                }
            }
        }
        return monsterExpStr;
    }

    public void setEnterWordEventId(Player player, String s) {
        enterWordEventId.put(player.getPlayerId(), s);
    }

    public String getEnterWordEventId(Player player) {
        return enterWordEventId.get(player.getPlayerId());
    }
}
