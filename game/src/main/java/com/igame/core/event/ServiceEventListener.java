package com.igame.core.event;

public interface ServiceEventListener {
    default EventType interestedType() {return null;}
    default EventType[] interestedTypes(){return null;}
    void handleEvent(Object event);
}
