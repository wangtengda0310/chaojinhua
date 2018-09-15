package com.igame.work.checkpoint.worldEvent;

import com.igame.core.ISFSModule;
import com.igame.core.di.Inject;
import com.igame.work.checkpoint.guanqia.CheckPointService;
import com.igame.work.monster.dto.Monster;
import com.igame.work.user.dto.Player;
import com.igame.work.user.load.ResourceService;

import java.util.List;

public class WorldEventService implements ISFSModule {
    @Inject
    private ResourceService resourceService;

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
}
