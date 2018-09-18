package com.igame.server;

import com.igame.core.ISFSModule;
import com.igame.core.data.ClassXmlDataLoader;
import com.igame.core.db.DBManager;
import com.igame.core.di.JarApplicationContext;
import com.igame.core.event.EventManager;
import com.igame.core.handler.ClientDispatcherHandler;
import com.igame.core.log.GoldLog;
import com.igame.core.quartz.JobManager;
import com.igame.core.quartz.TimeListener;
import com.igame.core.handler.EventDispatcherHandler;
import com.igame.work.fight.FightService;
import com.igame.work.fight.data.*;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.IClientRequestHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;


/**
 *
 * 类名以Extension结尾 在smart fox server 后台可以直接过滤出类名 方便配置
 *
 */
public class GameServerExtension extends SFSExtension {
	JarApplicationContext context = new JarApplicationContext(getLogger());

	private void register(int requestId, Class<? extends IClientRequestHandler> clazz) {
		addRequestHandler(String.valueOf(String.valueOf(requestId)), (IClientRequestHandler) context.cachedObjects.get(clazz));
	}

	/**注册SmartFoxServer的handler并注入ISFSModule属性*/
	private void register(Class<? extends ClientDispatcherHandler> clazz) {
		ClientDispatcherHandler handler = (ClientDispatcherHandler) context.cachedObjects.get(clazz);
		addRequestHandler(String.valueOf(handler.protocolId()), handler);
	}

	public static DBManager dbManager;

	@Override
	public void init() {
		try {

			ClassXmlDataLoader loader = new ClassXmlDataLoader("resource/");
			// 这几个都是static的还没重构成依赖注入
			FightService.effectData = loader.loadData(EffectData.class, "effectdata.xml");
			FightService.godsData = loader.loadData(GodsData.class, "godsdata.xml");
			FightService.godsEffectData = loader.loadData(GodsEffectData.class, "godseffect.xml");
			FightService.skillData = loader.loadData(SkillData.class, "skilldata.xml");
			FightService.skillLvData = loader.loadData(SkillLvData.class, "skillexp.xml");

			dbManager = (DBManager) context.cachedObjects.get(DBManager.class);
			dbManager.init(this);	// 数据库被其他模块init的时候依赖

			context.cachedObjects.values().stream()
					.filter(o->!o.equals(dbManager))	// 数据库被其他模块init的时候依赖
					.forEach(component->{
				if (component instanceof ISFSModule) {
					((ISFSModule)component).init(this);
					context.modules.add((ISFSModule) component);
				}

				if (component instanceof TimeListener) {
					JobManager.addJobListener((TimeListener) component);
				}

			});

			addEventHandler(SFSEventType.USER_VARIABLES_UPDATE, ((EventManager)context.cachedObjects.get(EventManager.class)).playerEventObserver());	// 利用USER_VARIABLES_UPDATE实现的服务器事件机制
			addEventHandler(SFSEventType.ROOM_VARIABLES_UPDATE, ((EventManager)context.cachedObjects.get(EventManager.class)).serviceEventListener());	// 利用ROOM_VARIABLES_UPDATE实现的服务器事件机制

			context.classOfInterface.get(ClientDispatcherHandler.class).forEach(this::register);

			context.classOfInterface.get(EventDispatcherHandler.class).forEach(this::addEventDispatcherHandler);
		} catch (Throwable e) {
			getLogger().error("extension error",e);
		}

	}

	private void addEventDispatcherHandler(Class<? extends EventDispatcherHandler> clazz) {
		try {
			EventDispatcherHandler h = context.injectObjectField(clazz.newInstance());
			addEventHandler(h.eventType(), h);
		} catch (InstantiationException|IllegalAccessException e) {
			throw new Error(e);
		}
	}

	@Override
	public void destroy(){
		super.destroy();
		context.modules.forEach(ISFSModule::destroy);
		GoldLog.info("GameServerExtension destroy");
	}

	public JarApplicationContext getApplicationContext() {
		return context;
	}
}
