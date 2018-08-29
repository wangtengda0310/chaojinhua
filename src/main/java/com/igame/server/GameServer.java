package com.igame.server;

import com.igame.core.ISFSModule;
import com.igame.core.SystemService;
import com.igame.core.data.DataManager;
import com.igame.core.db.DBManager;
import com.igame.core.event.EventManager;
import com.igame.core.handler.ReconnectedHandler;
import com.igame.core.log.GoldLog;
import com.igame.core.quartz.JobManager;
import com.igame.core.quartz.TimeListener;
import com.igame.work.MProtrol;
import com.igame.work.activity.meiriLiangfa.MeiriLiangfaHandler;
import com.igame.work.activity.sign.SignHandler;
import com.igame.work.activity.tansuoZhiLu.TansuoZhiLuActivityHandler;
import com.igame.work.chat.handler.*;
import com.igame.work.chat.service.PublicMessageService;
import com.igame.work.checkpoint.FightAgainHandler;
import com.igame.work.checkpoint.baozouShike.BallisticService;
import com.igame.work.checkpoint.baozouShike.handler.BallisticEndHandler;
import com.igame.work.checkpoint.baozouShike.handler.BallisticEnterHandler;
import com.igame.work.checkpoint.baozouShike.handler.BallisticMonsterHandler;
import com.igame.work.checkpoint.baozouShike.handler.BallisticRankHandler;
import com.igame.work.checkpoint.guanqia.handler.*;
import com.igame.work.checkpoint.mingyunZhiMen.GateService;
import com.igame.work.checkpoint.mingyunZhiMen.handler.*;
import com.igame.work.checkpoint.tansuo.handler.*;
import com.igame.work.checkpoint.worldEvent.handler.WorldEventEndHandler;
import com.igame.work.checkpoint.worldEvent.handler.WorldEventEnterHandler;
import com.igame.work.checkpoint.worldEvent.handler.WorldEventListHandler;
import com.igame.work.checkpoint.worldEvent.handler.WorldEventSaoHandler;
import com.igame.work.checkpoint.wujinZhiSen.handler.*;
import com.igame.work.checkpoint.xingheZhiYan.handler.TrialEndHandler;
import com.igame.work.checkpoint.xingheZhiYan.handler.TrialEnterHandler;
import com.igame.work.checkpoint.xingheZhiYan.handler.TrialSaleHandler;
import com.igame.work.checkpoint.xingheZhiYan.handler.WaKuangHandler;
import com.igame.work.checkpoint.xinmo.XinmoEndHandler;
import com.igame.work.checkpoint.xinmo.XinmoEnterHandler;
import com.igame.work.fight.handler.*;
import com.igame.work.fight.service.ArenaService;
import com.igame.work.fight.service.FightEffectService;
import com.igame.work.friend.handler.*;
import com.igame.work.gm.handler.GMHandler;
import com.igame.work.item.handler.*;
import com.igame.work.monster.handler.*;
import com.igame.work.quest.handler.QuestRewardHandler;
import com.igame.work.recharge.MockRechargeHandler;
import com.igame.work.recharge.RechargeListHandler;
import com.igame.work.serverList.ServerListHandler;
import com.igame.work.shop.handler.ReloadShopHandler;
import com.igame.work.shop.handler.ShopBuyHandler;
import com.igame.work.shop.handler.ShopInfoHandler;
import com.igame.work.shop.service.ShopService;
import com.igame.work.system.RankService;
import com.igame.work.turntable.handler.OneLotteryHandler;
import com.igame.work.turntable.handler.TenLotteryHandler;
import com.igame.work.turntable.handler.TurntableGetHandler;
import com.igame.work.turntable.handler.TurntableReloadHandler;
import com.igame.work.user.handler.*;
import com.igame.work.user.service.PlayerCacheService;
import com.igame.work.user.service.RobotService;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.IClientRequestHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Marcus.Z
 *
 */
public class GameServer extends SFSExtension {

	public static DBManager dbManager;

	private Map<Class, ISFSModule> services = new HashMap<>();

	/**
	 * 加载ISFSModule的实例
	 */
	private <T extends ISFSModule> T initService(Class<T> clazz) {
		try {
			T service = clazz.newInstance();

			if (ISFSModule.class.isAssignableFrom(clazz)) {
				service.init(this);
				services.put(clazz, service);
			}

			if (TimeListener.class.isAssignableFrom(clazz)) {
				JobManager.addJobListener((TimeListener) service);
			}

			return service;
		} catch (InstantiationException | IllegalAccessException e) {
			getLogger().error("init service {} error", clazz.getSimpleName(), e);
		}

		return null;
	}

	private <T extends IClientRequestHandler> T injectField(Class<T> clazz) {

		try {
			T handler = clazz.newInstance();

			for (Field field : clazz.getDeclaredFields()) {
				Class<?> fieldClass = field.getType();
				if (services.containsKey(fieldClass)) {
					field.setAccessible(true);
					field.set(handler, services.get(fieldClass));
				} else if (ISFSModule.class.isAssignableFrom(fieldClass)) {
					ISFSModule service = ((ISFSModule)fieldClass.newInstance());
					services.put(fieldClass, service);
					field.setAccessible(true);
					field.set(handler, services.get(fieldClass));
				}
			}

			return handler;
		} catch (InstantiationException | IllegalAccessException e) {
			getLogger().error("init handler {} error", clazz.getSimpleName(), e);
			return null;
		}

	}

	private void register(int requestId, Class<? extends IClientRequestHandler> clazz) {
		addRequestHandler(String.valueOf(String.valueOf(requestId)), injectField(clazz));
	}

	/**注册SmartFoxServer的handler并注入ISFSModule属性*/
	private void register(Class<? extends ReconnectedHandler> clazz) {
		ReconnectedHandler handler = injectField(clazz);
		addRequestHandler(String.valueOf(handler.protocolId()), handler);
	}

	@Override
	public void init() {
        DataManager dataManager = new DataManager();
        dataManager.load("resource/");

		dbManager = initService(DBManager.class);
		initService(SystemService.class);
		initService(RankService.class);
		initService(RobotService.class);
		initService(ArenaService.class);
		initService(BallisticService.class);
		initService(PlayerCacheService.class);
		initService(PublicMessageService.class);
		initService(FightEffectService.class);
		initService(GateService.class);

		initService(JobManager.class);

		JobManager.addJobListener(ShopService.ins());

		addEventHandler(SFSEventType.USER_VARIABLES_UPDATE, EventManager.playerEventObserver());	// 利用USER_VARIABLES_UPDATE实现的服务器事件机制
		addEventHandler(SFSEventType.ROOM_VARIABLES_UPDATE, EventManager.serviceEventListener());	// 利用ROOM_VARIABLES_UPDATE实现的服务器事件机制

		addEventHandler(SFSEventType.USER_LOGIN, LoginEventHandler.class);//自定义登录接口
		addEventHandler(SFSEventType.USER_LOGOUT, LOGINOUTEventHandler.class);
		addEventHandler(SFSEventType.USER_DISCONNECT, DisconnectEventHandler.class);
		addEventHandler(SFSEventType.PRIVATE_MESSAGE, PrivateMessageEventHandler.class);//私聊
		addEventHandler(SFSEventType.PUBLIC_MESSAGE, PublicMessageEventHandler.class);//公聊
		addEventHandler(SFSEventType.BUDDY_ADD, BuddyAddEventHandler.class);//加好友
		addEventHandler(SFSEventType.BUDDY_LIST_INIT, BuddyInitEventHandler.class);//加好友

		register(MProtrol.LOGOUT, LoginOutHandler.class);//客户端主动登出
		register(MProtrol.SERVER_LIST, ServerListHandler.class);//服务器列表
		register(MProtrol.PLAYER_ENTER, PlayerHandler.class);//选择服务器进入游戏
		register(MProtrol.RE_CONN, ReLoginHandler.class);
		register(MProtrol.HEART, HeartHandler.class);//心跳
		register(MProtrol.REQ_PUSH, ReqPushHndler.class);	// TODO refactor

		register(MonsterHandler.class);//怪物换阵
		register(GMHandler.class);//GM
		register(ItemHandler.class);//道具
		register(MonsterEVHandler.class);//怪物进化
		register(MonsterTUPOSHandler.class);//怪物基因单个突破
		register(MonsterTUPOChangeHandler.class);//怪物基因更换
		register(MonsterTUPOAllHandler.class);//怪物基因全部更换
		register(CheckEnterHandler.class);//进入某一关卡
		register(CheckEndHandler.class);//完成某一关卡
		register(CheckSaoDangHandler.class);//扫荡某一关卡
		register(CheckResHandler.class);//金币关卡领取
		register(MonsterNewHandler.class);//怪物新生
		register(MonsterLockHandler.class);//怪物锁
		register(MonsterChangeHandler.class);//怪物互换
		register(MonsterEquipHandler.class);//文章装备
		register(MonsterEquipDownAllHandler.class);//一键下文章
		register(ItemHeChengHandler.class);//装备合成
		register(ItemHeChengAllHandler.class);//一键合成
		register(TansuoListHandler.class);//探索
		register(TansuoMonsterHandler.class);//探索怪物
		register(TansuoUnLockHandler.class);//探索怪物
		register(TansuoStartHandler.class);//探索开始
		register(TansuoEndHandler.class);//探索结束
		register(WorldEventListHandler.class);//世界事件列表
		register(WorldEventEnterHandler.class);//世界事件进入
		register(WorldEventEndHandler.class);//世界事件完成
		register(WorldEventSaoHandler.class);//世界事件扫荡
		register(MonsterDataRefHandler.class);//MONSTER_REF
		register(TongHuaInfoHandler.class);//同化信息
		register(TongHuaOpenHandler.class);//同化打开
		register(TongHuaGetHandler.class);//同化领取
		register(TongHuaBuyHandler.class);//购买同化资源
		register(TongHuaCDHandler.class);//同化CD
		register(TongHuaEnterFightHandler.class);//同化进入战斗
		register(TongHuaFightHandler.class);//同化战斗
		register(TongHuaRefHandler.class);//同化刷新
		register(GodsUpHandler.class);//神灵升级
		register(GodsResetHandler.class);//神灵重置
		register(FightAgainHandler.class);//神灵重置
		register(ResBuyHandler.class);//资源购买
		register(MialReadHandler.class);//邮件读取
		register(MialGetHandler.class);//邮件领取
		register(MiaSendHandler.class);//邮件发送好友
		register(MialDelHandler.class);//邮件删除
		register(MiaSysSendHandler.class);//发系统邮件
		register(TuJianHandler.class);//遇到新怪物
		register(DrawGetHandler.class);//造物台造物
		register(PVPReadHandler.class);//匹配
		register(PVPChancelHandler.class);//取消匹配
		register(PVPReadCDHandler.class);//倒计时结束
		register(PVPGameLoadHandler.class);//加载游戏完毕
		register(FightCmdHandler.class);//战斗指令
		register(QuestRewardHandler.class);//领取任务奖励
		register(TrialEnterHandler.class);//进入星河之眼关卡战斗
		register(TrialEndHandler.class);//结束星河之眼关卡战斗
		register(WaKuangHandler.class);//星河之眼挖矿
		register(TrialSaleHandler.class);//购买星能(即星核之眼挑战次数)
		register(EndlessRefHandler.class);//无尽之森刷新
		register(EndlessEnterHandler.class);//进入无尽之森战斗
		register(EndlessEndHandler.class);//结束无尽之森战斗
		register(EndlessZhenHandler.class);//设置无尽之森自己怪物阵容
		register(EndlessNaiHandler.class);//无尽之森奶一口
		register(EndlessBufferHandler.class);//无尽之森重置BUFFER
		register(DeInfoHandler.class);//命运之门信息
		register(GateHandler.class);//快速选门
		register(GateEnterHandler.class);//命运之门进入
		register(GateEndHandler.class);//命运之门结束战斗
		register(GateResetHandler.class);//命运之门重新挑战
		register(GateRewardHandler.class);//命运之门领取
		register(GateRankHandler.class);//命运之门排行榜
		register(ShopInfoHandler.class);//获取商店信息
		register(ShopBuyHandler.class);//购买商品
		register(ReloadShopHandler.class);//刷新商店
		register(XinmoEnterHandler.class);//进入心魔关卡
		register(XinmoEndHandler.class);//结束心魔关卡战斗
		//register(MProtrol.JINGJI_ZHENG, MonsterDefendHandler.class);//竞技场防守阵容上阵下阵怪物
		register(EnterCheckHandler.class);//获取关卡信息
		register(ChangeHeadHandler.class);//更换头像
		register(ChangeFrameHandler.class);//更换头像框
		register(ModifyNicknameHandler.class);//更换昵称
		register(AreaInfoHandler.class);//获取竞技场信息
		register(BallisticRankHandler.class);//暴走时刻排行榜
		register(BallisticEnterHandler.class);//进入暴走时刻
		register(BallisticEndHandler.class);//结束暴走时刻
		register(BallisticMonsterHandler.class);//暴走时刻怪兽
		register(AreaPlayerInfoHandler.class);//获取对手信息
		register(AreaEnterHandler.class);//进入竞技场战斗
		register(AreaEndHandler.class);//结束竞技场战斗
		register(AreaRefHandler.class);//刷新竞技场新对手
		register(FriendFindHandler.class);//根据昵称查找好友
		register(FriendNominateHandler.class);//好友推荐
		register(FriendAddHandler.class);//添加好友
		register(FriendAgreeHandler.class);//同意好友请求
		register(FriendRefuseHandler.class);//拒绝好友请求
		register(FriendDeleteHandler.class);//删除好友
		register(FriendGivePhyHandler.class);//赠送体力
		register(FriendReceivePhyHandler.class);//领取体力
		register(FriendExploreHandler.class);//获取探索状态列表
		register(FriendGetExploreHandler.class);//获取探索详情
		register(FriendExploreAccHandler.class);//探索加速
		register(FriendAddToplimitHandler.class);//增加好友上限
		register(PublicMessageHandler.class);//获取世界消息
		register(PrivateMessageHandler.class);//获取私聊消息
		register(BanStrangersHandler.class);//拒绝或开放陌生人私聊
		register(GetPlayerInfoHandler.class);//拒绝或开放陌生人私聊
		register(MessageBoardAddHandler.class);//留言板新增留言
		register(MessageBoardGetHandler.class);//获取留言板
		register(MessageBoardModifyHandler.class);//修改留言板(点赞、取消点赞、反对、取消反对)
		register(ItemSaleHandler.class);//道具出售
		register(ItemSummonHandler.class);//召唤怪兽
		register(ItemAddToplimitHandler.class);//增加背包上限
		register(MonsterSkillHandler.class);//怪兽技能升级
		register(ItemGroupHandler.class);//道具合成
		register(ReplaceTeamHandler.class);//更换出战阵容
		register(VipBuyFristPackHandler.class);//会员购买礼包
		register(VipRecDayPackHandler.class);//会员领取每日礼包
		register(GodsFightHandler.class);//神灵出战
		register(TurntableGetHandler.class);//获取玩家幸运大转盘
		register(TurntableReloadHandler.class);//刷新幸运大转盘
		register(OneLotteryHandler.class);//幸运大转盘单抽
		register(TenLotteryHandler.class);//幸运大转盘十连抽
		register(MonsterListHandler.class);//怪物列表
		register(AreaBuyHandler.class);//幸运大转盘十连抽
		register(SignHandler.class);//签到活动
		register(MeiriLiangfaHandler.class);//每日两发活动
		register(TansuoZhiLuActivityHandler.class);//探索之路活动
		register(RechargeListHandler.class);//充值展示
		register(MockRechargeHandler.class);//模拟充值

	}

	@Override
	public void destroy(){
		services.values().forEach(ISFSModule::destroy);
		EventManager.clearAllListeners();
		GoldLog.info("GameServer destroy");
	}

}
