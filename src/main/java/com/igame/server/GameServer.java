package com.igame.server;

import com.igame.core.MProtrol;
import com.igame.core.db.DBManager;
import com.igame.core.log.GoldLog;
import com.igame.core.quartz.JobManager;
import com.igame.util.SystemService;
import com.igame.work.activity.ActivityHandler;
import com.igame.work.activity.sign.SignHandler;
import com.igame.work.chat.handler.*;
import com.igame.work.chat.service.PublicMessageService;
import com.igame.work.checkpoint.handler.*;
import com.igame.work.checkpoint.service.BallisticService;
import com.igame.work.fight.handler.*;
import com.igame.work.fight.service.ArenaService;
import com.igame.work.fight.service.FightEffectService;
import com.igame.work.friend.handler.*;
import com.igame.work.gm.handler.GMHandler;
import com.igame.work.gm.handler.TestHandler;
import com.igame.work.item.handler.*;
import com.igame.work.monster.handler.*;
import com.igame.work.quest.handler.QuestRewardHandler;
import com.igame.work.shop.handler.ReloadShopHandler;
import com.igame.work.shop.handler.ShopBuyHandler;
import com.igame.work.shop.handler.ShopInfoHandler;
import com.igame.work.system.RankService;
import com.igame.work.turntable.handler.OneLotteryHandler;
import com.igame.work.turntable.handler.TenLotteryHandler;
import com.igame.work.turntable.handler.TurntableGetHandler;
import com.igame.work.turntable.handler.TurntableReloadHandler;
import com.igame.work.user.handler.*;
import com.igame.work.user.service.PlayerCacheService;
import com.igame.work.user.service.RobotService;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.SFSExtension;


/**
 * 
 * @author Marcus.Z
 *
 */
public class GameServer extends SFSExtension {
	

	@Override
	public void init() {
		
		DBManager.getInstance();
		
		addEventHandler(SFSEventType.USER_LOGIN, LoginEventHandler.class);//自定义登录接口
		
		addEventHandler(SFSEventType.USER_LOGOUT, LOGINOUTEventHandler.class);
		
		addEventHandler(SFSEventType.USER_DISCONNECT, DisconnectEventHandler.class);
		
		addEventHandler(SFSEventType.PRIVATE_MESSAGE, PrivateMessageEventHandler.class);//私聊
		
		addEventHandler(SFSEventType.PUBLIC_MESSAGE, PublicMessageEventHandler.class);//公聊
		
		addEventHandler(SFSEventType.BUDDY_ADD, BuddyAddEventHandler.class);//加好友
		
		addEventHandler(SFSEventType.BUDDY_LIST_INIT, BuddyInitEventHandler.class);//加好友
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.SERVER_LIST), ServerListHandler.class);//服务器列表
		
		addRequestHandler("200", LoginOutHandler.class);//客户端主动登出
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.PLAYER_ENTER), PlayerHandler.class);//选择服务器进入游戏
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.RE_CONN), ReLoginHandler.class);
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.CHANGE_TEAM), MonsterHandler.class);//怪物换阵
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TEST), TestHandler.class);//TEST
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.GM), GMHandler.class);//GM
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.HEART), HeartHandler.class);//心跳
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.ITEM_USE), ItemHandler.class);//道具
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MONSTER_EV), MonsterEVHandler.class);//怪物进化
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MONSTER_TUPO_S), MonsterTUPOSHandler.class);//怪物基因单个突破
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MONSTER_TUPO_C), MonsterTUPOChangeHandler.class);//怪物基因更换
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MONSTER_TUPO_A), MonsterTUPOAllHandler.class);//怪物基因全部更换
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.CHECKPOINT_ENTER), CheckEnterHandler.class);//进入某一关卡
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.CHECKPOINT_END), CheckEndHandler.class);//完成某一关卡
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.CHECKPOINT_SAO), CheckSaoDangHandler.class);//扫荡某一关卡
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.CHECKPOINT_RES_GET), CheckResHandler.class);//金币关卡领取
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MONSTER_NEW), MonsterNewHandler.class);//怪物新生
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MONSTER_LOCK), MonsterLockHandler.class);//怪物锁
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MONSTER_CHANGE), MonsterChangeHandler.class);//怪物互换
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.ITEM_EQ), MonsterEquipHandler.class);//文章装备

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.ITEM_EQ_ALL), MonsterEquipDownAllHandler.class);//一键下文章

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.ITEM_HE), ItemHeChengHandler.class);//装备合成
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.ITEM_HE_ALL), ItemHeChengAllHandler.class);//一键合成
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TANGSUO_LIST), TangSuoListHandler.class);//探索
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TANGSUO_MONSTER), TangSuoMonsterHandler.class);//探索怪物
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TANGSUO_MONSTER_UN), TangSuoUnLockHandler.class);//探索怪物
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TANGSUO_START), TangStartHandler.class);//探索开始
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TANGSUO_END), TangEndHandler.class);//探索结束
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.WWORDEVENT_LIST), WordEventListHandler.class);//世界事件列表
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.WWORDEVENT_ENTER), WordEventEnterHandler.class);//世界事件进入
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.WWORDEVENT_END), WordEventEndHandler.class);//世界事件完成
				
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.WWORDEVENT_SAO), WordEventSaoHandler.class);//世界事件扫荡
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MONSTER_REF), MonsterDataRefHandler.class);//MONSTER_REF
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TONGHUA_INFO), TongHuaInfoHandler.class);//同化信息
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TONGHUA_OPEN), TongHuaOpenHandler.class);//同化打开
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TONGHUA_GET), TongHuaGetHandler.class);//同化领取
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TONGHUAINFO_BUY), TongHuaBuyHandler.class);//购买同化资源
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TONGHUA_CD_BUY), TongHuaCDHandler.class);//同化CD
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TONGHUA_ENTER_FIGHT), TongHuaEnterFightHandler.class);//同化进入战斗
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TONGHUA_FIGHT), TongHuaFightHandler.class);//同化战斗
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TONGHUA_CD_REF), TongHuaRefHandler.class);//同化刷新
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.Gods_UP), GodsUpHandler.class);//神灵升级
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.Gods_RESET), GodsResetHandler.class);//神灵重置
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.BUG_RES), ResBuyHandler.class);//资源购买
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MAIL_READ), MialReadHandler.class);//邮件读取
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MAIL_GET), MialGetHandler.class);//邮件领取
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MAIL_NEW), MiaSendHandler.class);//邮件发送好友
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MAIL_DEL), MialDelHandler.class);//邮件删除
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MAIL_NEW_SYS), MiaSysSendHandler.class);//发系统邮件
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MEET_NEW_MONSTER), TuJianHandler.class);//遇到新怪物
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.DRAW_GET), DrawGetHandler.class);//造物台造物
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.F_P_S), PVPReadHandler.class);//匹配
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.F_P_C), PVPChancelHandler.class);//取消匹配
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.F_P_D), PVPReadCDHandler.class);//倒计时结束
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.F_LOAD_E), PVPGameLoadHandler.class);//加载游戏完毕
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.F_CMD), FightCmdHandler.class);//战斗指令
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.QUEST_REWARD), QuestRewardHandler.class);//领取任务奖励
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TRIAL_ENTER), TrialEnterHandler.class);//进入星河之眼关卡战斗
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TRIAL_END), TrialEndHandler.class);//结束星河之眼关卡战斗
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TRIAL_WA), WaKuangHandler.class);//星河之眼挖矿

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.TRIAL_SALE), TrialSaleHandler.class);//购买星能(即星核之眼挑战次数)
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.WU_REF), EndlessRefHandler.class);//无尽之森刷新
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.WU_ENTER), EndlessEnterHandler.class);//进入无尽之森战斗
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.WU_END), EndlessEndHandler.class);//结束无尽之森战斗
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.WUZHENG_YES), EndlessZhenHandler.class);//设置无尽之森自己怪物阵容
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.WU_NAI), EndlessNaiHandler.class);//无尽之森奶一口
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.WU_BUFFER), EndlessBufferHandler.class);//无尽之森重置BUFFER
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.DE_INFO), DeInfoHandler.class);//命运之门信息

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.GATE_INFO), GateHandler.class);//快速选门
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.GATE_ENTER), GateEnterHandler.class);//命运之门进入
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.GATE_END), GateEndHandler.class);//命运之门结束战斗
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.GATE_RESET), GateResetHandler.class);//命运之门重新挑战
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.GATE_REWARD), GateRewardHandler.class);//命运之门领取
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.GATE_RANKS), GateRankHandler.class);//命运之门排行榜

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.SHOP_INFO), ShopInfoHandler.class);//获取商店信息

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.SHOP_BUY), ShopBuyHandler.class);//购买商品

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.SHOP_Reload), ReloadShopHandler.class);//刷新商店
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.XINGMO_ENTER), XingMoEnterHandler.class);//进入心魔关卡
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.XINGMO_END), XiongMoEndHandler.class);//结束心魔关卡战斗
		
		//addRequestHandler(MProtrol.toStringProtrol(MProtrol.JINGJI_ZHENG), MonsterDefendHandler.class);//竞技场防守阵容上阵下阵怪物
			
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.ENTER_CHECK), EnterCheckHandler.class);//获取关卡信息

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.HEAD_CHANGE), ChangeHeadHandler.class);//更换头像

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRAME_CHANGE), ChangeFrameHandler.class);//更换头像框
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.AREA_INFO), AreaInfoHandler.class);//获取竞技场信息

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.BALLISTIC_RANKS), BallisticRankHandler.class);//暴走时刻排行榜

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.BALLISTIC_ENTER), BallisticEnterHandler.class);//进入暴走时刻

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.BALLISTIC_END), BallisticEndHandler.class);//结束暴走时刻

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.BALLISTIC_MONSTER), BallisticMonsterHandler.class);//暴走时刻怪兽
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.AREA_P_INFO), AreaPlayerInfoHandler.class);//获取对手信息
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.AREA_ENTER), AreaEnterHandler.class);//进入竞技场战斗
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.AREA_END), AreaEndHandler.class);//结束竞技场战斗
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.AREA_REF), AreaRefHandler.class);//刷新竞技场新对手

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_FIND), FriendFindHandler.class);//根据昵称查找好友

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_NOMINATE), FriendNominateHandler.class);//好友推荐

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_ADD), FriendAddHandler.class);//添加好友

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_AGREE), FriendAgreeHandler.class);//同意好友请求

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_REFUSE), FriendRefuseHandler.class);//拒绝好友请求

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_DELETE), FriendDeleteHandler.class);//删除好友

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_PHY_GIVE), FriendGivePhyHandler.class);//赠送体力

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_PHY_RECEIVE), FriendReceivePhyHandler.class);//领取体力

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE), FriendExploreHandler.class);//获取探索状态列表

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE_GET), FriendGetExploreHandler.class);//获取探索详情

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_EXPLORE_ACC), FriendExploreAccHandler.class);//探索加速

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.FRIEND_TOPLIMIT_ADD), FriendAddToplimitHandler.class);//增加好友上限

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MESSAGE_WORLD), PublicMessageHandler.class);//获取世界消息

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MESSAGE_PRIVATE), PrivateMessageHandler.class);//获取私聊消息

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MESSAGE_BAN_STRANGERS), BanStrangersHandler.class);//拒绝或开放陌生人私聊

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MESSAGE_PLAYERINFO), GetPlayerInfoHandler.class);//拒绝或开放陌生人私聊

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MESSAGE_BOARD_ADD), MessageBoardAddHandler.class);//留言板新增留言

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MESSAGE_BOARD_GET), MessageBoardGetHandler.class);//获取留言板

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MESSAGE_BOARD_MODIFY), MessageBoardModifyHandler.class);//修改留言板(点赞、取消点赞、反对、取消反对)

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.ITEM_SALE), ItemSaleHandler.class);//道具出售

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.ITEM_SUMMON), ItemSummonHandler.class);//召唤怪兽

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.ITEM_ADD_TOPLIMIT), ItemAddToplimitHandler.class);//增加背包上限

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MONSTER_SKILL), MonsterSkillHandler.class);//怪兽技能升级

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.ITEM_GROUP), ItemGroupHandler.class);//道具合成
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.REQ_PUSH), ReqPushHndler.class);

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.REPLACE_TEAM), ReplaceTeamHandler.class);//更换出战阵容

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.VIP_FRIST_PACK), VipBuyFristPackHandler.class);//会员购买礼包

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.VIP_DAY_PACK), VipRecDayPackHandler.class);//会员领取每日礼包
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.GODS_FIGHT), GodsFightHandler.class);//神灵出战

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.LUCKTABLE_GET), TurntableGetHandler.class);//获取玩家幸运大转盘

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.LUCKTABLE_RELOAD), TurntableReloadHandler.class);//刷新幸运大转盘

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.LUCKTABLE_LOTTERY_ONE), OneLotteryHandler.class);//幸运大转盘单抽

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.LUCKTABLE_LOTTERY_TEN), TenLotteryHandler.class);//幸运大转盘十连抽
		
		addRequestHandler(MProtrol.toStringProtrol(MProtrol.MONSTER_INFO), MonsterListHandler.class);//怪物列表

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.AREA_BUY), AreaBuyHandler.class);//幸运大转盘十连抽

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.ACTICITY), ActivityHandler.class);//幸运大转盘十连抽

		addRequestHandler(MProtrol.toStringProtrol(MProtrol.SIGN), SignHandler.class);//签到

		JobManager.ins();

		SystemService.ins().loadData();

		RankService.ins().loadData();

		RobotService.ins().load();

		ArenaService.ins().loadRobot();

		ArenaService.ins().loadRank(true);

		BallisticService.ins().loadData();

		PlayerCacheService.ins().loadData();

		PublicMessageService.ins().load();

		FightEffectService.ins().initFightEffect();
	}
	
	@Override
	public void destroy(){

		SystemService.ins().saveData();
		GoldLog.info("GameServer destroy");
	}

}
