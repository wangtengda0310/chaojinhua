package com.igame.server;

import com.igame.core.ISFSModule;
import com.igame.core.SystemService;
import com.igame.core.data.DataManager;
import com.igame.core.db.DBManager;
import com.igame.core.di.Inject;
import com.igame.core.di.Injectable;
import com.igame.core.event.EventManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.core.quartz.JobManager;
import com.igame.core.quartz.TimeListener;
import com.igame.work.chat.handler.PrivateMessageEventHandler;
import com.igame.work.chat.handler.PublicMessageEventHandler;
import com.igame.work.chat.service.PublicMessageService;
import com.igame.work.checkpoint.baozouShike.BallisticService;
import com.igame.work.checkpoint.mingyunZhiMen.GateService;
import com.igame.work.fight.service.ArenaService;
import com.igame.work.fight.service.FightEffectService;
import com.igame.work.friend.handler.BuddyAddEventHandler;
import com.igame.work.friend.handler.BuddyInitEventHandler;
import com.igame.work.shop.service.ShopService;
import com.igame.work.system.RankService;
import com.igame.work.user.handler.DisconnectEventHandler;
import com.igame.work.user.handler.LOGINOUTEventHandler;
import com.igame.work.user.handler.LoginEventHandler;
import com.igame.work.user.service.PlayerCacheService;
import com.igame.work.user.service.RobotService;
import com.smartfoxserver.v2.core.SFSEventType;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;
import com.smartfoxserver.v2.extensions.IClientRequestHandler;
import com.smartfoxserver.v2.extensions.SFSExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;


/**
 *
 * 类名以Extension结尾 在smart fox server 后台可以直接过滤出类名 方便配置
 *
 */
public class GameServerExtension extends SFSExtension {

	public static DBManager dbManager;

	private Map<Class, Object> services = new HashMap<>();
	private Set<ISFSModule> modules = new HashSet<>();

	/**
	 * 加载ISFSModule的实例
	 */
	private <T extends ISFSModule> T initService(Class<T> clazz) {
		try {
			T service = clazz.newInstance();

			if (ISFSModule.class.isAssignableFrom(clazz)) {
				service.init(this);
				services.put(clazz, service);
				injectField(service);
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

	private Object injectField(Object service) {

		try {
			for (Field field : service.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				if(field.get(service)!=null) {
					continue;
				}
				Class<?> fieldClass = field.getType();
				if(!field.isAnnotationPresent(Inject.class)
						&& !Injectable.class.isAssignableFrom(fieldClass)) {
					continue;
				}

				if (services.containsKey(fieldClass)) {
					field.set(service, services.get(fieldClass));
				} else {
					Object s = fieldClass.newInstance();
					services.put(fieldClass, s);
					injectField(s);
					if (s instanceof ISFSModule) {
						modules.add((ISFSModule)s);
					}
					field.set(service, s);
				}
			}

		} catch (InstantiationException | IllegalAccessException e) {
			getLogger().error("init service {} error", service, e);
		}

		return service;
	}
	private <T extends IClientRequestHandler> T injectField(Class<T> clazz) {

		try {
			T handler = clazz.newInstance();

			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				if(field.get(handler)!=null) {
					continue;
				}
				Class<?> fieldClass = field.getType();
				if (!field.isAnnotationPresent(Inject.class)
						&& !Injectable.class.isAssignableFrom(fieldClass)) {
					continue;
				}

				if (services.containsKey(fieldClass)) {
					field.set(handler, services.get(fieldClass));
				} else {
					Object s = fieldClass.newInstance();
					services.put(fieldClass, s);
					injectField(s);
					if (s instanceof ISFSModule) {
						modules.add((ISFSModule)s);
					}
					field.set(handler, s);
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
	private void register(Class<? extends BaseHandler> clazz) {
		BaseHandler handler = injectField(clazz);
		addRequestHandler(String.valueOf(handler.protocolId()), handler);
	}

	private HashSet<Class> classesInJarFile = new HashSet<>();

	public GameServerExtension() {
		try {
			new JarFile(getClass().getProtectionDomain().getCodeSource().getLocation().getFile())
					.stream()
					.map(JarEntry::getName)
					.filter(name -> name.endsWith(".class"))
					.map(name -> name.substring(0,name.indexOf(".class")).replace("/","."))
					.map(n-> {
						try {
							return Class.forName(n);
						} catch (ClassNotFoundException e) {
							throw new RuntimeException(e);
						}
					})
					.filter(c-> !Modifier.isAbstract(c.getModifiers()))
					.forEach(c->classesInJarFile.add(c));

			summarize();
		} catch (IOException e) {
			trace(e);
		}
	}

	private Map<Class,Set<Class>> classOfInterface = new HashMap<>();

	private void summarize(Class thisClass, Class superClass) {
		if (Modifier.isAbstract(thisClass.getModifiers())) {
			return;
		}
		Stream.concat(Stream.of(superClass, superClass.getSuperclass()), Arrays.stream(superClass.getInterfaces()))
				.filter(Objects::nonNull)
				.forEach(i->classOfInterface.computeIfAbsent(i,interfaceClass->new HashSet<>()).add(thisClass));

		Stream.concat(Stream.of(superClass.getSuperclass()), Arrays.stream(superClass.getInterfaces()))
				.filter(Objects::nonNull)
				.forEach(i->summarize(thisClass,i));
	}
	private void summarize() {
		classesInJarFile.forEach(c->summarize(c,c));
	}

	@Override
	public void init() {
		try {
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

			classOfInterface.get(BaseHandler.class).forEach(this::register);
		} catch (Throwable e) {
			trace(e);
		}

	}

	@Override
	public void destroy(){
		super.destroy();
		modules.forEach(ISFSModule::destroy);
		EventManager.clearAllListeners();
		GoldLog.info("GameServerExtension destroy");
	}

}
