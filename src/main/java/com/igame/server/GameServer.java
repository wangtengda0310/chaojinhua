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

	private void addRequestHandler(int requestId, Class<? extends IClientRequestHandler> clazz) {
		addRequestHandler(String.valueOf(String.valueOf(requestId)), clazz);
	}

	/**注册SmartFoxServer的handler并注入ISFSModule属性*/
	private void addRequestHandler(Class<? extends ReconnectedHandler> clazz) {
		try {
			ReconnectedHandler handler = clazz.newInstance();

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
			addRequestHandler(String.valueOf(handler.protocolId()), handler);
		} catch (InstantiationException | IllegalAccessException e) {
			getLogger().error("init handler {} error", clazz.getSimpleName(), e);
		}

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

		addRequestHandler(MProtrol.LOGOUT, LoginOutHandler.class);//客户端主动登出
		addRequestHandler(MProtrol.SERVER_LIST, ServerListHandler.class);//服务器列表
		addRequestHandler(MProtrol.PLAYER_ENTER, PlayerHandler.class);//选择服务器进入游戏
		addRequestHandler(MProtrol.RE_CONN, ReLoginHandler.class);
		addRequestHandler(MProtrol.HEART, HeartHandler.class);//心跳
		addRequestHandler(MProtrol.REQ_PUSH, ReqPushHndler.class);	// TODO refactor

		addRequestHandler(MonsterHandler.class);//怪物换阵
		addRequestHandler(GMHandler.class);//GM
		addRequestHandler(ItemHandler.class);//道具
		addRequestHandler(MonsterEVHandler.class);//怪物进化
		addRequestHandler(MonsterTUPOSHandler.class);//怪物基因单个突破
		addRequestHandler(MonsterTUPOChangeHandler.class);//怪物基因更换
		addRequestHandler(MonsterTUPOAllHandler.class);//怪物基因全部更换
		addRequestHandler(CheckEnterHandler.class);//进入某一关卡
		addRequestHandler(CheckEndHandler.class);//完成某一关卡
		addRequestHandler(CheckSaoDangHandler.class);//扫荡某一关卡
		addRequestHandler(CheckResHandler.class);//金币关卡领取
		addRequestHandler(MonsterNewHandler.class);//怪物新生
		addRequestHandler(MonsterLockHandler.class);//怪物锁
		addRequestHandler(MonsterChangeHandler.class);//怪物互换
		addRequestHandler(MonsterEquipHandler.class);//文章装备
		addRequestHandler(MonsterEquipDownAllHandler.class);//一键下文章
		addRequestHandler(ItemHeChengHandler.class);//装备合成
		addRequestHandler(ItemHeChengAllHandler.class);//一键合成
		addRequestHandler(TansuoListHandler.class);//探索
		addRequestHandler(TansuoMonsterHandler.class);//探索怪物
		addRequestHandler(TansuoUnLockHandler.class);//探索怪物
		addRequestHandler(TansuoStartHandler.class);//探索开始
		addRequestHandler(TansuoEndHandler.class);//探索结束
		addRequestHandler(WorldEventListHandler.class);//世界事件列表
		addRequestHandler(WorldEventEnterHandler.class);//世界事件进入
		addRequestHandler(WorldEventEndHandler.class);//世界事件完成
		addRequestHandler(WorldEventSaoHandler.class);//世界事件扫荡
		addRequestHandler(MonsterDataRefHandler.class);//MONSTER_REF
		addRequestHandler(TongHuaInfoHandler.class);//同化信息
		addRequestHandler(TongHuaOpenHandler.class);//同化打开
		addRequestHandler(TongHuaGetHandler.class);//同化领取
		addRequestHandler(TongHuaBuyHandler.class);//购买同化资源
		addRequestHandler(TongHuaCDHandler.class);//同化CD
		addRequestHandler(TongHuaEnterFightHandler.class);//同化进入战斗
		addRequestHandler(TongHuaFightHandler.class);//同化战斗
		addRequestHandler(TongHuaRefHandler.class);//同化刷新
		addRequestHandler(GodsUpHandler.class);//神灵升级
		addRequestHandler(GodsResetHandler.class);//神灵重置
		addRequestHandler(FightAgainHandler.class);//神灵重置
		addRequestHandler(ResBuyHandler.class);//资源购买
		addRequestHandler(MialReadHandler.class);//邮件读取
		addRequestHandler(MialGetHandler.class);//邮件领取
		addRequestHandler(MiaSendHandler.class);//邮件发送好友
		addRequestHandler(MialDelHandler.class);//邮件删除
		addRequestHandler(MiaSysSendHandler.class);//发系统邮件
		addRequestHandler(TuJianHandler.class);//遇到新怪物
		addRequestHandler(DrawGetHandler.class);//造物台造物
		addRequestHandler(PVPReadHandler.class);//匹配
		addRequestHandler(PVPChancelHandler.class);//取消匹配
		addRequestHandler(PVPReadCDHandler.class);//倒计时结束
		addRequestHandler(PVPGameLoadHandler.class);//加载游戏完毕
		addRequestHandler(FightCmdHandler.class);//战斗指令
		addRequestHandler(QuestRewardHandler.class);//领取任务奖励
		addRequestHandler(TrialEnterHandler.class);//进入星河之眼关卡战斗
		addRequestHandler(TrialEndHandler.class);//结束星河之眼关卡战斗
		addRequestHandler(WaKuangHandler.class);//星河之眼挖矿
		addRequestHandler(TrialSaleHandler.class);//购买星能(即星核之眼挑战次数)
		addRequestHandler(EndlessRefHandler.class);//无尽之森刷新
		addRequestHandler(EndlessEnterHandler.class);//进入无尽之森战斗
		addRequestHandler(EndlessEndHandler.class);//结束无尽之森战斗
		addRequestHandler(EndlessZhenHandler.class);//设置无尽之森自己怪物阵容
		addRequestHandler(EndlessNaiHandler.class);//无尽之森奶一口
		addRequestHandler(EndlessBufferHandler.class);//无尽之森重置BUFFER
		addRequestHandler(DeInfoHandler.class);//命运之门信息
		addRequestHandler(GateHandler.class);//快速选门
		addRequestHandler(GateEnterHandler.class);//命运之门进入
		addRequestHandler(GateEndHandler.class);//命运之门结束战斗
		addRequestHandler(GateResetHandler.class);//命运之门重新挑战
		addRequestHandler(GateRewardHandler.class);//命运之门领取
		addRequestHandler(GateRankHandler.class);//命运之门排行榜
		addRequestHandler(ShopInfoHandler.class);//获取商店信息
		addRequestHandler(ShopBuyHandler.class);//购买商品
		addRequestHandler(ReloadShopHandler.class);//刷新商店
		addRequestHandler(XinmoEnterHandler.class);//进入心魔关卡
		addRequestHandler(XinmoEndHandler.class);//结束心魔关卡战斗
		//addRequestHandler(MProtrol.JINGJI_ZHENG, MonsterDefendHandler.class);//竞技场防守阵容上阵下阵怪物
		addRequestHandler(EnterCheckHandler.class);//获取关卡信息
		addRequestHandler(ChangeHeadHandler.class);//更换头像
		addRequestHandler(ChangeFrameHandler.class);//更换头像框
		addRequestHandler(ModifyNicknameHandler.class);//更换昵称
		addRequestHandler(AreaInfoHandler.class);//获取竞技场信息
		addRequestHandler(BallisticRankHandler.class);//暴走时刻排行榜
		addRequestHandler(BallisticEnterHandler.class);//进入暴走时刻
		addRequestHandler(BallisticEndHandler.class);//结束暴走时刻
		addRequestHandler(BallisticMonsterHandler.class);//暴走时刻怪兽
		addRequestHandler(AreaPlayerInfoHandler.class);//获取对手信息
		addRequestHandler(AreaEnterHandler.class);//进入竞技场战斗
		addRequestHandler(AreaEndHandler.class);//结束竞技场战斗
		addRequestHandler(AreaRefHandler.class);//刷新竞技场新对手
		addRequestHandler(FriendFindHandler.class);//根据昵称查找好友
		addRequestHandler(FriendNominateHandler.class);//好友推荐
		addRequestHandler(FriendAddHandler.class);//添加好友
		addRequestHandler(FriendAgreeHandler.class);//同意好友请求
		addRequestHandler(FriendRefuseHandler.class);//拒绝好友请求
		addRequestHandler(FriendDeleteHandler.class);//删除好友
		addRequestHandler(FriendGivePhyHandler.class);//赠送体力
		addRequestHandler(FriendReceivePhyHandler.class);//领取体力
		addRequestHandler(FriendExploreHandler.class);//获取探索状态列表
		addRequestHandler(FriendGetExploreHandler.class);//获取探索详情
		addRequestHandler(FriendExploreAccHandler.class);//探索加速
		addRequestHandler(FriendAddToplimitHandler.class);//增加好友上限
		addRequestHandler(PublicMessageHandler.class);//获取世界消息
		addRequestHandler(PrivateMessageHandler.class);//获取私聊消息
		addRequestHandler(BanStrangersHandler.class);//拒绝或开放陌生人私聊
		addRequestHandler(GetPlayerInfoHandler.class);//拒绝或开放陌生人私聊
		addRequestHandler(MessageBoardAddHandler.class);//留言板新增留言
		addRequestHandler(MessageBoardGetHandler.class);//获取留言板
		addRequestHandler(MessageBoardModifyHandler.class);//修改留言板(点赞、取消点赞、反对、取消反对)
		addRequestHandler(ItemSaleHandler.class);//道具出售
		addRequestHandler(ItemSummonHandler.class);//召唤怪兽
		addRequestHandler(ItemAddToplimitHandler.class);//增加背包上限
		addRequestHandler(MonsterSkillHandler.class);//怪兽技能升级
		addRequestHandler(ItemGroupHandler.class);//道具合成
		addRequestHandler(ReplaceTeamHandler.class);//更换出战阵容
		addRequestHandler(VipBuyFristPackHandler.class);//会员购买礼包
		addRequestHandler(VipRecDayPackHandler.class);//会员领取每日礼包
		addRequestHandler(GodsFightHandler.class);//神灵出战
		addRequestHandler(TurntableGetHandler.class);//获取玩家幸运大转盘
		addRequestHandler(TurntableReloadHandler.class);//刷新幸运大转盘
		addRequestHandler(OneLotteryHandler.class);//幸运大转盘单抽
		addRequestHandler(TenLotteryHandler.class);//幸运大转盘十连抽
		addRequestHandler(MonsterListHandler.class);//怪物列表
		addRequestHandler(AreaBuyHandler.class);//幸运大转盘十连抽
		addRequestHandler(SignHandler.class);//签到活动
		addRequestHandler(MeiriLiangfaHandler.class);//每日两发活动
		addRequestHandler(TansuoZhiLuActivityHandler.class);//探索之路活动
		addRequestHandler(RechargeListHandler.class);//充值展示
		addRequestHandler(MockRechargeHandler.class);//模拟充值

	}

	@Override
	public void destroy(){
		services.values().forEach(ISFSModule::destroy);
		EventManager.clearAllListeners();
		GoldLog.info("GameServer destroy");
	}

}
