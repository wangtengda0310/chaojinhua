package com.igame.core.event;

import com.igame.work.user.dto.Player;

public interface PlayerEventObserver {
    EventType interestedType();
    void observe(Player eventOwner, Object event);
}
