package com.igame.core.event;

import java.lang.annotation.*;

/**注在Map上，玩家下线时，自动移除，key应为playerId，注意下线保存数据的时候会出现并发问题*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RemoveOnLogout {
    String value() default "getPlayerId";
}
