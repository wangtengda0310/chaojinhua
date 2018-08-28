package com.igame.core.event;

public abstract class EventService {
    {
        PlayerEventObserver playerEventObserver = observeEvent();
        if (playerEventObserver != null) {
            EventManager.playerEventObservers.put(getClass().getSimpleName(), playerEventObserver);
        }
        ServiceEventListener playerEventListener = listenEvent();
        if (playerEventListener != null) {
            EventManager.serviceEventListeners.put(getClass().getSimpleName(), playerEventListener);
        }
    }

    /**监听玩家相关的数据*/
    protected PlayerEventObserver observeEvent() {
        return null;
    }

    /**监听事件*/
    protected ServiceEventListener listenEvent() {
        return null;
    }

}
