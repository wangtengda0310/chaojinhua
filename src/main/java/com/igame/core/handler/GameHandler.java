package com.igame.core.handler;

import com.smartfoxserver.v2.entities.User;

public interface GameHandler {
    default void sendError(int errorCode, String cmdName, RetVO vo, User user) {

        vo.setState(1);
        vo.setErrCode(errorCode);

        send(cmdName,vo, user);
    }

    default void sendSucceed(String cmdName, RetVO vo, User user) {

        vo.setState(0);

        send(cmdName,vo, user);
    }

    void send(String cmdName, RetVO vo, User user);

    default EventListener eventListener() {
        return null;
    }
    interface EventListener {
        void handleEvent(Object event);
    }
}
