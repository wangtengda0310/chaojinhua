package com.igame.core.event;

import com.igame.core.SessionManager;
import com.igame.work.user.dto.Player;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.variables.SFSUserVariable;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;

import java.util.HashMap;
import java.util.Map;

public class EventManager {
    public static Map<String, EventListener> listeners = new HashMap<>();

    /** 接受用户相关的事件并预处理后传给GameHandler.EventListener */
    public static BaseServerEventHandler serverEventListener() {
        return new BaseServerEventHandler() {
            @Override
            public void handleServerEvent(ISFSEvent isfsEvent) {
                if (isfsEvent.getType()== SFSEventType.USER_VARIABLES_UPDATE && isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP)!=null) {
                    User user = null;
                    if(isfsEvent.getParameter(SFSEventParam.USER) instanceof User) {
                        user = (User) isfsEvent.getParameter(SFSEventParam.USER);
                    }
                    if(isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP) instanceof Map) {
                        Map parameters = (Map) isfsEvent.getParameter(SFSEventParam.VARIABLES_MAP);
                        Object parameter = parameters.get("last.event");

                        if(parameter instanceof SFSUserVariable) {
                            for (EventListener listener: listeners.values()) {

                                Player player = SessionManager.ins().getSession(Long.parseLong(user.getName()));
                                if(player != null) {
                                    listener.handleEvent(player, ((SFSUserVariable) parameter).getValue());
                                } else {
                                    // TODO 是否加载缓存或数据库？
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    public static void clearAllListeners() {
        listeners.clear();
    }
}
