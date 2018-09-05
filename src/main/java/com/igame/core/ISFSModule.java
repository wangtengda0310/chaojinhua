package com.igame.core;

import com.igame.core.di.Injectable;
import com.igame.server.GameServerExtension;

/**这个接口的实现类会根据class被注入到Handler的属性中*/
public interface ISFSModule extends Injectable {
    class ExtensionHolder {
        public GameServerExtension SFSExtension;
    }
    ExtensionHolder extensionHolder = new ExtensionHolder();
    default void init(GameServerExtension extension) {
        this.extensionHolder.SFSExtension = extension;
        init();
    }
    default void destroy(){}
    default void init(){}
}
