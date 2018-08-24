package com.igame.work;

/**
 * 
 * @author Marcus.Z
 *
 */
public class ErrorCode {
	
	public static final int ERROR = 1;//异常错误

	public static final int PARAMS_INVALID = 2;//参数异常错误
	
	public static final int SERVER_NOT = 1001;//服务器不存在
	
	public static final int NEWPLAYER_ERROR = 1002;//保存新角色错误
	
	public static final int DIAMOND_NOT_ENOUGH = 1004;//钻石不足
	
	public static final int GOLD_NOT_ENOUGH = 1005;//金币不足
	
	public static final int ITEM_NOT_ENOUGH = 1006;//道具不足
	
	public static final int PHYSICA_NOT_ENOUGH = 1007;//体力不足
	
	public static final int TONGHUA_NOT_ENOUGH = 1008;//同化资源不够
	
	public static final int BUY_COUNT_NOT_ENOUGH = 1009;//今日购买次数已用完
	
	public static final int XING_NOT_ENOUGH = 1010;//星能不足
	
	public static final int WA_NOT_ENOUGH = 1011;//今日可挖矿次数已用完
	
	public static final int LEVEL_NOT = 1012;//玩家等级不够，未开放此系统

	public static final int WUJIN_NOT_ENOUGH = 1013;//无尽积分不足

	public static final int DOUJI_NOT_ENOUGH = 1014;//斗技场积分不足

	public static final int QIYUAN_NOT_ENOUGH = 1015;//起源殿积分不足

	public static final int BULUO_NOT_ENOUGH = 1016;//部落积分不足

	public static final int YUANZHENG_NOT_ENOUGH = 1017;//远征积分不足

	public static final int XUANSHANG_NOT_ENOUGH = 1018;//悬赏积分不足

	public static final int MSG_FAILED = 1019;//消息发送失败

	public static final int SAO_NOT_ENOUGH = 1020;//扫荡券不足

	public static final int TEAM_NOT = 2001;//Team编号非法

	public static final int MONSTER_NOT = 2003;//玩家没有此怪物

	public static final int LEVEL_MAX = 2008;//已达最大等级
	
	public static final int LEVEL_NOT_PLAYER = 2009;//不能超过人物等级
	
	public static final int ITEM_NOT_EXIT = 2010;//道具不存在
	
	public static final int MONSTER_EV_MAX = 2011;//已进化到最大品级。
	
	public static final int LEVEL_NOT_ENOUGH = 2012;//等级不够
	
	public static final int NOT_NEXT_OBJECT = 2013;//无法进化到此品质
	
	public static final int MONSTER_RANK_ERROR = 2014;//此阶数无法突破
	
	public static final int MONSTER_RANK_EXIT = 2015;//此阶已经突破（如需更换请选择换一个）
	
	public static final int MONSTER_TUPO_NOT = 2016;//此阶尚未突破
	
	public static final int MONSTER_CHANGE_NOT = 2017;//此阶数无法更换
	
	public static final int MONSTER_JIYINGTYPE_ERROR = 2018;//期望改造的类型错误
	
	public static final int CHECKPOINT_ENTER_ERROR = 2019;//无法进入此关卡（前置关卡未通过）
	
	public static final int CHECKPOINT_END_ERROR = 2020;//无法完成此关卡（涉嫌作弊）
	
	public static final int CHECKPOINT_RESNOT_EXIT = 2021;//无此金币关卡
	
	public static final int MONSTER_CON_NOT = 2022;//未达成新生需要的条件（怪物）
	
	public static final int MONSTER_RA_NOTE = 2023;//怪物品质不同无法互换
	
	public static final int EQ_NOT_XIE = 2024;//无装备可卸载
	
	public static final int EQ_LOCATION_NOT = 2025;//位置未解锁
	
	public static final int EQ_LOCATION_ZHUANG = 2026;//装备类型于装备位置不符
	
	public static final int EQ_HECHENG_CANT = 2027;//此纹章无法合成
	
	public static final int EQ_HECHENG_TYPECANT = 2028;//不能用此方式合成
	
	public static final int TANG_MON_FULL = 2029;//请先下阵再上阵
	
	public static final int TANG_MON_YET = 2030;//次怪物已经在其他探索地图上阵
	
	public static final int TANG_MON_UN_PRE = 2031;//请先解锁前一个位置
	
	public static final int TANG_MON_ONE = 2032;//请上阵怪物
	
	public static final int TANG_TIME_NOT = 2033;//时间未到
	
	public static final int TODAY_COUNT_NOTENOUGH = 2034;//今日可挑战次数已用完
	
	public static final int TONGHUA_NOTOPEN = 2035;//该点不可打开
	
	public static final int TONGHUA_NOTGET = 2036;//该点不可领取
	
	public static final int TONGHUA_NOTCD = 2037;//该点无法消除CD
	
	public static final int TONGHUA_CD_NOT = 2038;//免费刷新倒计时未到
	
	public static final int GODS_MAX = 2039;//神灵已达最大等级
	
	public static final int GODS_ZERO = 2040;//等级为0无法重置
	
	public static final int QUEST_REWARD_NOT = 2041;//任务无法领取
	
	public static final int WUZHENG_ERROR = 2050;//当前无尽之森阵容已设置
	
	public static final int NAI_ERROR = 2051;//奶次数已经用完
	
	public static final int WUBUFFER_ERROR = 2052;//重置buffer个数和已拥有BUFFER数不一致
	
	public static final int  WUZHENG_COUNT_ERROR = 2053;//上阵怪物必须至少4个
	
	public static final int  WUZHENG_HPFULL = 2054;//怪物HP均已满
	
	public static final int  GATE_NOT = 2055;//今日已不可挑战
	
	public static final int  XINGMO_LEAVEL = 2056;//心魔已经离开
	
	public static final int  XINGMO_EXIT = 2057;//先打败心魔才可挑战此关卡
	
	public static final int  AREA_NOT_LOW = 2058;//不可挑战比自己名次低的玩家
	
	public static final int  AREA_NOT_SELF = 2059;//不可挑战自己
	

	public static final int SHOP_LOCK = 3001;//商店未解锁

	public static final int SHOP_STOCK = 3002;//可购买数量不足

	public static final int SHOP_NOT_RELOAD = 3003;//当日刷新次数不足

	public static final int HEAD_UNLOCK = 3010;//头像未解锁

	public static final int FRAME_UNLOCK = 3011;//头像框未解锁

	public static final int BALLISTIC_LOCK = 3020;//暴走时刻未解锁

	public static final int BALLISTIC_NOT_ENTER = 3021;//当日挑战次数不足

	public static final int EXPLORE_NO_ACC = 3031;//不可加速

	public static final int EXPLORE_NOT_ENOUGH = 3032;//好友探索加速次数不足

	public static final int RECEIVEPHY_NOT_ENOUGH = 3033;//好友体力领取次数不足

	public static final int MESSAGE_TOO_LONG = 3041;//消息过长

	public static final int SHORT_INTERVAL_TIME = 3042;//间隔时间过短

	public static final int RECIPIENT_NOT_ONLINE = 3043;//对方不在线

	public static final int IS_BAN_STRANGERS = 3044;//对方拒绝陌生人私聊

	public static final int BAGSPACE_ALREADY_FULL = 3050;//背包空间不足
	
	public static final int CHECKCOUNT_NOT_ENOUGH = 3051;//该关卡今日挑战次数不足

	public static final int VIP_LV_LACK = 3061;//VIP等级不足

	public static final int PACK_PURCHASED = 3062;//礼包已购买或已领取
	public static final int CAN_NOT_RECEIVE = 3063;//不能领取活动奖励

}
