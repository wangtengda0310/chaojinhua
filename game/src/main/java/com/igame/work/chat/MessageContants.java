package com.igame.work.chat;

/**
 * @author xym
 */
public class MessageContants {


    //发送者为系统
   /* public static final int MSG_TYPE_ = 1;    //
    public static final int MSG_TYPE_ = 2;    //
    public static final int MSG_TYPE_ = 3;    //
    public static final int MSG_TYPE_ = 4;    //
    public static final int MSG_TYPE_ = 5;    //*/

    //发送者为玩家
    public static final int MSG_TYPE_WORLD = 6;    //世界
    public static final int MSG_TYPE_HORN = 7;    //喇叭
    public static final int MSG_TYPE_CLUB = 8;    //工会
    public static final int MSG_TYPE_FRIEND = 9;    //好友私聊
    public static final int MSG_TYPE_STRANGER = 10;    //陌生人私聊


    public static final int MSG_LENGTH_MAX = 160;    //最大字节数
    public static final int CACHE_MAX = 20;    //最大缓存数量

    //留言板类型
    public static final int MSG_BOARD_TYPE_XING = 1;    //星核之眼
    public static final int MSG_BOARD_TYPE_CHECKPOINT = 2;    //冒险关卡
    public static final int MSG_BOARD_TYPE_WORLDEVENT = 3;    //世界事件
    public static final int MSG_BOARD_TYPE_LONGMIAN = 4;    //龙眠海峡
    public static final int MSG_BOARD_TYPE_MONSTER = 5;    //怪物图鉴

    //留言板操作
    public static final String MSG_BOARD_OPE_LIKE = "like";    //点赞
    public static final String MSG_BOARD_OPE_DISLIKE = "disLike";    //反对

}
