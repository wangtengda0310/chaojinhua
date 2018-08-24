package com.igame.core.handler;

import com.igame.core.event.EventListener;
import com.igame.core.event.EventManager;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

import java.util.Collections;

/**
 * @author xym
 */
public abstract class BaseHandler extends BaseClientRequestHandler implements GameHandler {

    {
        EventListener eventListener = listenEvent();
        if (eventListener != null) {
            EventManager.listeners.put(getClass().getSimpleName(), eventListener);
        }
    }

    private EventListener listenEvent() {
        return null;
    }

    /**
     * 发送用户相关的事件
     * 使用的是setUserVariables 所以会受到用户变量限制 SmartFoxServer默认允许每个玩家使用5个变量
     */
    protected void fireEvent(Player player, Object event) {
        SFSUserVariable e = new SFSUserVariable("last.event", event, false, true);
        // 如果user为null,SmartFoxServer发布出事件，会报空指针，可以点进去看一下代码
        getApi().setUserVariables(player.getUser(), Collections.singletonList(e), false, true);
    }

    @Override
    public void warn(String warn, Exception e) {
        this.getLogger().warn(warn, e);
    }

    @Override
    public void sendClient(User user, String cmdName, ISFSObject params) {
        send(cmdName, params, user);
    }
}
