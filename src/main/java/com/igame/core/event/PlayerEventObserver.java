package com.igame.core.event;

import com.igame.work.user.dto.Player;

public interface PlayerEventObserver {
    default EventType interestedType() {return null;}
    default EventType[] interestedTypes() {return new EventType[0];}
    void observe(Player eventOwner, Object event);
}
