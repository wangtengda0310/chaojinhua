package com.igame.core;

import com.igame.core.di.Injectable;
import com.igame.server.GameServer;

/**这个接口的实现类会根据class被注入到Handler的属性中*/
public interface ISFSModule extends Injectable {
    class ExtensionHolder {
        public GameServer SFSExtension;
    }
    ExtensionHolder holder = new ExtensionHolder();
    default void init(GameServer extension) {
        this.holder.SFSExtension = extension;
        init();
    }
    default void destroy(){}
    void init();
}
