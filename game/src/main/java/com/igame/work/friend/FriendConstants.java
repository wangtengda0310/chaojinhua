package com.igame.work.friend;

/**
 * @author xym
 *
 * 好友常量
 */
public class FriendConstants {

    public static final int FRIEND_DEFAULT_MAX_COUNT = 20;  // 初始好友上线
    public static final int FRIEND_DEFAULT_RECPHY_COUNT = 20;  // 默认可领取体力

    public static final int FRIEND_STATE_SUCC = 0;  //操作成功
    public static final int FRIEND_STATE_NOTEXIST = 1;  //角色不存在
    public static final int FRIEND_STATE_ADDED = 2;  //角色已添加
    public static final int FRIEND_STATE_REQED = 3;  //角色已请求
    public static final int FRIEND_STATE_MYUP = 4;  //当前角色好友数量已达上限
    public static final int FRIEND_STATE_OTHERUP = 5;   //对方好友数量已达上限

    public static final int FRIEND_STATE_NO_HELP = 0;  //不可帮助
    public static final int FRIEND_STATE_CAN_HELP = 1;  //可帮助

}
