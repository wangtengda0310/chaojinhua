package com.igame.core.event;

import java.lang.annotation.*;

/**注在Map上，玩家下线时，自动移除，key应为playerId*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RemoveOnLogout {
    String value() default "getPlayerId";
}
