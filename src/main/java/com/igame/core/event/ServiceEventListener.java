package com.igame.core.event;

public interface ServiceEventListener {
    EventType interestedType();
    void handleEvent(Object event);
}
