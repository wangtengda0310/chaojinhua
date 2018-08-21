package com.igame.core;

/**
 * 
 * @author Marcus.Z
 *
 */
public class MProtrol {
	
	
	public static final int REQ_PUSH = 300;//请求推送历史消息
	
	public static final int HEART = 400;//心跳
	
	public static final int GM = 500;//GM命令
	
	public static final int LOGINOUT = 501;//重复登录被下线
	
	public static final int TEST = 600;//测试
	
	public static final int RE_CONN = 1001;//重连登录
	
	public static final int SERVER_LIST = 1002;//服务器列表
	
	public static final int PLAYER_ENTER = 1003;//选择服务器进入游戏
	
	public static final int PLAYER_INDE_UP = 1004;//阵位开放通知
	
	public static final int PLAYER_LEVEL_UP = 1005;//玩家经验、级别更新通知
	
	public static final int ITEM_UPDATE = 1006;//道具更新
	
	public static final int GOLD_UPDATE = 1007;//玩家货币更新

	public static final int HEAD_UPDATE = 1008;//玩家头像更新

	public static final int FRAME_UPDATE = 1009;//玩家头像框更新

	public static final int MYSTICAL_SHOP_UPDATE = 1010;//玩家神秘商店更新

	public static final int GENERAL_SHOP_UPDATE = 1011;//玩家一般商店更新

	public static final int FRIEND_LIST_UPDATE = 1012;//玩家好友列表更新

	public static final int FRIEND_REQ_UPDATE = 1013;//玩家好友请求列表更新

	public static final int FRIEND_PHY_UPDATE = 1014;//玩家体力赠送更新

	public static final int MYSTICAL_SHOP_EXP = 1015;//玩家神秘商店经验更新

	public static final int TEAM_UPDATE = 1016;//玩家阵容更新

	public static final int VIP_UPDATE = 1017;//玩家会员等级与特权更新

	public static final int TURNTABLE_UPDATE = 1018;//玩家幸运大转盘推送

	public static final int MONSTER_INFO = 1020;//怪物信息
	
	public static final int MONSTER_LSIT = 1021;//怪物信息

	public static final int FRIENDS_UPDATE = 1025;//玩家好友更新

	public static final int CHANGE_TEAM = 2001;//怪物换阵

	public static final int MONSTER_UPDATE = 2002;//玩家怪物更新新
	
	public static final int MONSTER_EV = 2003;//怪物净化
	
	public static final int MONSTER_TUPO_S = 2004;//怪物单个突破
	
	public static final int MONSTER_TUPO_C = 2005;//怪物基因更换
	
	public static final int MONSTER_TUPO_A = 2006;//怪物基因全部更换
	
	public static final int MONSTER_NEW = 2007;//怪物新生
	
	public static final int MONSTER_LOCK = 2008;//怪物锁
	
	public static final int MONSTER_CHANGE = 2009;//怪物互换
	
	public static final int ITEM_USE = 2010;//使用道具
	
	public static final int ITEM_EQ = 2011;//装备
	
	public static final int ITEM_HE = 2012;//装备合成
	
	public static final int ITEM_HE_ALL = 2013;//一键合成

	public static final int ITEM_EQ_ALL = 2059;//一键下纹章

	public static final int MONSTER_REF = 2014;//怪物数据获取

	public static final int ITEM_SALE = 2015;//道具出售

	public static final int ITEM_SUMMON = 2016;//召唤怪兽

	public static final int ITEM_ADD_TOPLIMIT = 2017;//增加背包上限

	public static final int ITEM_GROUP = 2018;//道具合成
	
	public static final int ENTER_CHECK = 2019;//进入关卡
	
//	public static final int CHECKPOINT_LIST = 2020;//副本关卡列表
	
	public static final int CHECKPOINT_ENTER = 2021;//进入此关卡
	
	public static final int CHECKPOINT_END = 2022;//战斗结束
	
	public static final int CHECKPOINT_SAO = 2023;//扫荡关卡
	
	public static final int CHECKPOINT_RES_UPDATE = 2024;//金币关卡更新
	
	public static final int CHECKPOINT_RES_GET = 2025;//金币关卡获取
	
	public static final int XINGMO_UP = 2026;//心魔更新
	
	public static final int TANGSUO_LIST = 2027;//探索列表
	
	public static final int TANGSUO_MONSTER = 2028;//探索怪物操作
	
	public static final int TANGSUO_MONSTER_UN = 2029;//探索位置解锁
	
	public static final int TANGSUO_START = 2030;//探索开始
	
	public static final int TANGSUO_END = 2031;//探索结束
	
	public static final int WWORDEVENT_LIST = 2032;//世界事件列表
	
	public static final int WWORDEVENT_ENTER = 2033;//进入世界事件关卡
	
	public static final int WWORDEVENT_END= 2034;//完成世界事件关卡
	
	public static final int WWORDEVENT_SAO= 2036;//扫荡世界事件关卡
	
	public static final int TONGHUA_INFO= 2035;//同化信息
	
	public static final int TONGHUA_OPEN= 2037;//同化节点打开
	
	public static final int TONGHUA_UPDATE= 2038;//同化推送更新
	
	public static final int TONGHUA_GET= 2039;//领取节点
	
	public static final int TONGHUAINFO_UPDATE= 2040;//同化属性更新
	
	public static final int TONGHUAINFO_BUY= 2041;//购买同化资源
	
	public static final int TONGHUA_CD_BUY= 2042;//金币去倒计时
	
	public static final int TONGHUA_FIGHT= 2043;//完成怪物节点战斗
	
	public static final int TONGHUA_CD_REF= 2044;//刷新同化
	
	public static final int Gods_UP= 2045;//神灵升级
	
	public static final int Gods_RESET= 2055;//神灵重置
	
	public static final int Gods_TUI= 2060;//神灵推送
	
	public static final int BUG_RES= 2046;//资源购买
	
	public static final int MAIL_READ= 2047;//读取邮件
	
	public static final int MAIL_GET= 2048;//领取附件
	
	public static final int MAIL_DEL= 2049;//删除邮件
	
	public static final int MAIL_NEW= 2050;//发好友邮件
	
	public static final int MAIL_TUI= 2051;//新邮件推送
	
	public static final int MAIL_NEW_SYS= 2052;//系统发邮件
	
	public static final int MEET_NEW_MONSTER= 2053;//遇到新怪
	
	public static final int MEET_UPDATE= 2054;//遇到新怪
	
	public static final int DRAW_UPDATE= 2056;//造物台更新
	
	public static final int DRAW_GET= 2057;//造物台造物
	
	public static final int TONGHUA_ENTER_FIGHT= 2058;//进入同化怪物关卡
	// 2059 在前面用到了 一键下纹章
	public static final int CHECK_UNLOCK= 2070;//关卡解锁
	
	public static final int CD_DOWN= 2071;//倒计时开始
	
	
	public static final int F_P_S= 3000;//战斗匹配
	
	public static final int F_P_E= 3001;//匹配成功
	
	public static final int F_P_D= 3002;//倒计时完毕
	
	public static final int F_P_ENTER= 3003;//开始加载游戏
	
	public static final int F_LOAD_E= 3004;//加载完毕
	
	public static final int F_START= 3005;//游戏开始
	
	public static final int F_P_C= 3006;//取消匹配
	
	public static final int F_CMD= 3100;//战斗指令
	
	public static final int QUEST_UPDATE= 3200;//任务更新新
	
	public static final int QUEST_REWARD= 3201;//领取任务成就奖励
	
	public static final int QUEST_REF= 3202;//刷新成就任务
	
	public static final int TRIAL_ENTER= 3205;//进入星河之眼战斗
	
	public static final int TRIAL_END= 3206;//结束星河之眼战斗
	
	public static final int TRIAL_UPDATE= 3207;//推送星河之眼数据改变
	
	public static final int TRIAL_WA= 3208;//星河之眼挖矿
	
	public static final int WU_UPDATE= 3209;//推送无尽之森关卡数据更新
	
	public static final int WU_REF= 3210;//无尽之森关卡刷新
	
	public static final int WU_ENTER= 3211;//进入无尽之森战斗
	
	public static final int WU_END= 3212;//结束无尽之森战斗
	
	public static final int WUZHENG_UPDATE= 3213;//推送无尽之森自己怪物阵容更新

	public static final int WUZHENG_YES= 3214;//确定无尽之森阵容
	
	public static final int WUNAI_UPDATE= 3215;//推送无尽之森奶更新
	
	public static final int WUBUFFER_UPDATE= 3216;//推送无尽之森buffer更新
	
	public static final int WU_NAI= 3217;//无尽之森奶
	
	public static final int WU_BUFFER= 3218;//无尽之森重置BUFFER
	
	public static final int WU_RESET= 3219;//无尽之森免费重置次数更新
	
	public static final int DE_INFO = 3220;//命运之门信息
	
	public static final int DE_UPDATE = 3221;//命运之门信息更新
	
	public static final int GATE_INFO = 3222;//快速选门
	
	public static final int GATE_UPDATE = 3223;//新的门信息
	
	public static final int GATE_ENTER = 3224;//进入某门

	public static final int GATE_END = 3225;//结束某门战斗
	
	public static final int GATE_RESET = 3226;//重新挑战
	
	public static final int GATE_REWARD = 3227;//命运之门领取
	
	public static final int GATE_RANKS = 3228;//命运之门排行

	public static final int XINGMO_ENTER = 3230;//进入心魔关卡
	
	public static final int XINGMO_END = 3231;//结束心魔关卡战斗
	
	public static final int AREA_COUNT = 3236;//推送竞技场已用挑战次数
	
	public static final int AREA_INFO = 3237;//获取竞技场信息
	
	public static final int AREA_P_INFO = 3238;//获取对手信息
	
	public static final int AREA_ENTER = 3239;//进入竞技场战斗
	
	public static final int AREA_END = 3240;//结束竞技场战斗
	
	public static final int AREA_REF = 3241;//刷新竞技场新对手

	public static final int TRIAL_SALE = 3242;//星河之眼购买星能

	public static final int MONSTER_SKILL = 3243;//怪兽技能升级




	public static final int BALLISTIC_RANKS = 3301;//获取暴走时刻排行榜

	public static final int BALLISTIC_ENTER = 3302;//进入暴走时刻

	public static final int BALLISTIC_END = 3303;//结束暴走时刻

	public static final int BALLISTIC_MONSTER = 3304;//请求怪物

	public static final int FRIEND_FIND = 3401;//根据昵称查找好友

	public static final int FRIEND_NOMINATE = 3402;//好友推荐

	public static final int FRIEND_AGREE = 3403;//同意好友请求

	public static final int FRIEND_REFUSE = 3404;//拒绝好友请求

	public static final int FRIEND_DELETE = 3405;//删除好友

	public static final int FRIEND_PHY_GIVE = 3406;//赠送体力

	public static final int FRIEND_PHY_RECEIVE = 3407;//领取体力

	public static final int FRIEND_EXPLORE_ACC = 3408;//探索加速

	public static final int FRIEND_TOPLIMIT_ADD = 3409;//增加好友上限

	public static final int FRIEND_ADD= 3410;//添加好友(即发送好友请求)

	public static final int FRIEND_EXPLORE = 3411;//获取探索列表

	public static final int FRIEND_EXPLORE_GET = 3412;//获取探索详情

	public static final int MESSAGE_WORLD = 3501;//获取世界消息

	public static final int MESSAGE_PRIVATE = 3502;//获取私聊消息

	public static final int MESSAGE_BAN_STRANGERS = 3503;//拒绝陌生人私聊

	public static final int MESSAGE_ERROR = 3504;//消息发送失败推送

	public static final int MESSAGE_PLAYERINFO = 3505;//获取角色信息

	public static final int MESSAGE_BOARD_ADD = 3506;//留言板新增留言

	public static final int MESSAGE_BOARD_GET = 3507;//获取留言板

	public static final int MESSAGE_BOARD_MODIFY = 3508;//修改留言(点赞、取消点赞、反对、取消反对)

	public static final int SHOP_INFO = 4001;//获取商店信息

	public static final int SHOP_BUY = 4002;//购买商品

	public static final int SHOP_Reload = 4003;//刷新商店

	public static final int HEAD_CHANGE = 4010;//更换头像

	public static final int FRAME_CHANGE = 4011;//更换头像框

	public static final int MODIFY_NICKNAME = 4012;//更换昵称

	public static final int VIP_DAY_PACK = 4021;//VIP领取每日礼包

	public static final int VIP_FRIST_PACK = 4022;//VIP购买限定礼包

	public static final int REPLACE_TEAM = 4023;//更换阵容
	
	public static final int GODS_FIGHT = 4024;//阵容更换上阵神灵

	public static final int LUCKTABLE_GET = 4025;//获取幸运大转盘

	public static final int LUCKTABLE_RELOAD = 4026;//刷新幸运大转盘

	public static final int LUCKTABLE_LOTTERY_ONE = 4027;//幸运大转盘单抽

	public static final int LUCKTABLE_LOTTERY_TEN = 4028;//幸运大转盘十连抽

	public static final int AREA_BUY = 4029;//购买竞技场挑战次数

	public static final int ACTICITY = 4050;//活动
	public static final int SIGN = 4051;//签到活动



	public static String toStringProtrol(int p){
		return String.valueOf(p);		
	}
	
	public static int toIntProtrol(String p){
		return Integer.parseInt(p);
	}
	

}
