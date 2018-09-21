package com.igame.work;

import com.igame.core.event.EventType;

public enum PlayerEvents implements EventType {
    DEFAULT
    , RECHARGE
    , AGREE_FRIEND
    , DELETE_FRIEND
    , MODIFY_NAME
    , ARENA_RANK
    , UPDATE_GATE_RANK
    , UPDATE_BALLISTIC_RANK
    , RESET_ONCE// TODO move to ServiceEvents
    , CONSUME_DIAMOND
    , CONSUME_GOLD
    , CONSUME_ITEM
    , OFF_LINE  // todo 发送这个事件后会移除session 可能导致后面接受事件的地方取不到Player对象
    , LEVEL_UP
    , GOT_MONSTER
    , DRAW_BY_DIAMOND
    , GOD_LEVEL_UP
    , TURN_TABLE
    , GATE_RANK
}
