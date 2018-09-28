package com.igame.core.di;

import com.igame.core.ISFSModule;
import com.igame.core.event.EventService;
import com.igame.core.event.EventType;
import com.igame.core.event.PlayerEventObserver;
import com.igame.core.event.RemoveOnLogout;
import com.igame.work.PlayerEvents;
import com.igame.work.user.dto.Player;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

public class AnnotationReflectionHandler extends EventService implements ISFSModule {
    @Override
    public void init() {
        super.init();
        extensionHolder.SFSExtension.getApplicationContext().addAnnotationHandler(RemoveOnLogout.class, this);
    }

    Collection<Map<Long, Object>> maps = new LinkedList<>();    // 空map的equals是true 这不能用HashSet
    void handle(Map<Long,Object> f, Annotation o){
        maps.add(f);
    }

    @Override
    protected PlayerEventObserver playerObserver() {
        return new PlayerEventObserver() {
            @Override
            public EventType interestedType() {
                return PlayerEvents.OFF_LINE;
            }

            @Override
            public void observe(Player player, EventType eventType, Object event) {
                maps.forEach(m->m.remove(player.getPlayerId()));
            }
        };
    }
}
