package com.igame.core.handler;

import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

/**
 * @author xym
 */
public abstract class BaseEventHandler extends BaseServerEventHandler implements GameHandler {

    @Override
    public void warn(String warn, Exception e) {

        this.getLogger().warn(warn, e);
    }

    @Override
    public void sendClient(User user, String cmdName, ISFSObject params) {
        send(cmdName, params, user);
    }
}
