package com.igame.core.event;

import com.igame.work.user.dto.Player;

@FunctionalInterface
public interface EventListener {
    void handleEvent(Player eventOwner, Object event);
}
