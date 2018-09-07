package com.igame.work;

import com.igame.core.event.EventType;

public enum PlayerEvents implements EventType {
    DEFAULT
    , RECHARGE
    , AGREE_FRIEND
    , DELETE_FRIEND
    , MODIFY_NAME
    , ARENA_RANK
    , UPDATE_M_RANK
    , UPDATE_BALLISTIC_RANK
    , RESET_ONCE// TODO move to ServiceEvents
    , CONSUME_DIAMOND
    , CONSUME_GOLD
    , CONSUME_ITEM
    , OFF_LINE
}
