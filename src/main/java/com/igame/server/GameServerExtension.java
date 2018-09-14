package com.igame.server;

import com.igame.core.ISFSModule;
import com.igame.core.data.DataManager;
import com.igame.core.db.DBManager;
import com.igame.core.di.Inject;
import com.igame.core.di.Injectable;
import com.igame.core.event.EventManager;
import com.igame.core.handler.BaseHandler;
import com.igame.core.log.GoldLog;
import com.igame.core.quartz.JobManager;
import com.igame.core.quartz.TimeListener;
import com.igame.sfsAdaptor.EventDispatcherHandler;
import com.smartfoxserver.v2.core.SFSEventType;
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

	private Set<ISFSModule> modules = new HashSet<>();

	private HashSet<Class> classesInJarFile = new HashSet<>();
	public Map<Class,Object> cachedObjects = new HashMap<>();
	private Map<Class,Set<Class>> classOfInterface = new HashMap<>();

	private <T> T injectObjectField(T needInjectField) {

		try {
			doReflact(needInjectField);

			return needInjectField;
		} catch (InstantiationException | IllegalAccessException e) {
			getLogger().error("init {} error", needInjectField, e);
			throw new Error(e);
		}
	}

	private <T> T injectHandlerClassField(Class<T> clazz) {

		try {
			T handler = (T) cachedObjects.get(clazz);

			doReflact(handler);

			return handler;
		} catch (InstantiationException | IllegalAccessException e) {
			getLogger().error("init {} error", clazz.getSimpleName(), e);
			throw new Error(e);
		}

	}

	private <T> void doReflact(T component) throws IllegalAccessException, InstantiationException {
        Class<?> aClass = component.getClass();
	    do {

			for (Field field : aClass.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.get(component) != null) {
					continue;
				}
				Class<?> fieldClass = field.getType();
				if (!field.isAnnotationPresent(Inject.class)
						&& !Injectable.class.isAssignableFrom(fieldClass)) {
					continue;
				}

				if (cachedObjects.containsKey(fieldClass)) {
					field.set(component, cachedObjects.get(fieldClass));	// 递归出口
				} else {
					Object fieldObject = fieldClass.newInstance();
					cachedObjects.put(fieldClass, fieldObject); 			// 需要先放入components 不然下面递归会死循环
					field.set(component, fieldObject);
					injectObjectField(fieldObject);			 			// 这里递归了，如果上面没先放进components会死循环
				}
			}

        } while ((aClass = aClass.getSuperclass())!=null);
	}

	private void register(int requestId, Class<? extends IClientRequestHandler> clazz) {
		addRequestHandler(String.valueOf(String.valueOf(requestId)), (IClientRequestHandler) cachedObjects.get(clazz));
	}

	/**注册SmartFoxServer的handler并注入ISFSModule属性*/
	private void register(Class<? extends BaseHandler> clazz) {
		BaseHandler handler = (BaseHandler) cachedObjects.get(clazz);
		addRequestHandler(String.valueOf(handler.protocolId()), handler);
	}

	// 房到classOfInterface里
	private void summarize(Class thisClass, Class superClass) {
		Stream.concat(Stream.of(superClass.getSuperclass()), Arrays.stream(superClass.getInterfaces()))
				.filter(Objects::nonNull)
				.forEach(i->summarize(thisClass,i));

        if (!Modifier.isAbstract(thisClass.getModifiers())) {
			Stream.concat(Stream.of(superClass, superClass.getSuperclass()), Arrays.stream(superClass.getInterfaces()))
					.filter(Objects::nonNull)
					.forEach(i->classOfInterface.computeIfAbsent(i,interfaceClass->new HashSet<>()).add(thisClass));
        }
	}

	public GameServerExtension() {
		try {
			new JarFile(getClass().getProtectionDomain().getCodeSource().getLocation().getFile())
					.stream()
					.map(JarEntry::getName)
					.filter(name -> name.endsWith(".class"))
					.filter(name -> !name.contains("$"))
					.map(name -> name.substring(0,name.indexOf(".class")).replace("/","."))
					.map(n-> {
						try {
							return Class.forName(n);
						} catch (ClassNotFoundException e) {
							throw new RuntimeException(e);
						}
					})
					.filter(c-> !Modifier.isAbstract(c.getModifiers()))
					.peek(c->{
						try {
							if (Injectable.class.isAssignableFrom(c)) {
								cachedObjects.put(c, c.newInstance());
							}
						} catch (Throwable e) {
							getLogger().warn("{}",c);
						}
					})
					.forEach(c->classesInJarFile.add(c));

			new HashSet<>(cachedObjects.values()).forEach(this::injectObjectField);
			classesInJarFile.forEach(c->summarize(c,c));
		} catch (IOException e) {
			trace(e);
		}
	}

	public static DBManager dbManager;

	@Override
	public void init() {
		try {
			DataManager dataManager = new DataManager();
			dataManager.load("resource/");

			dbManager = (DBManager) cachedObjects.get(DBManager.class);
			dbManager.init(this);	// 数据库被其他模块init的时候依赖

			cachedObjects.values().stream()
					.filter(o->!o.equals(dbManager))	// 数据库被其他模块init的时候依赖
					.forEach(component->{
				if (component instanceof ISFSModule) {
					((ISFSModule)component).init(this);
					modules.add((ISFSModule) component);
				}

				if (component instanceof TimeListener) {
					JobManager.addJobListener((TimeListener) component);
				}

			});

			addEventHandler(SFSEventType.USER_VARIABLES_UPDATE, ((EventManager)cachedObjects.get(EventManager.class)).playerEventObserver());	// 利用USER_VARIABLES_UPDATE实现的服务器事件机制
			addEventHandler(SFSEventType.ROOM_VARIABLES_UPDATE, ((EventManager)cachedObjects.get(EventManager.class)).serviceEventListener());	// 利用ROOM_VARIABLES_UPDATE实现的服务器事件机制

			classOfInterface.get(BaseHandler.class).forEach(this::register);

			classOfInterface.get(EventDispatcherHandler.class).forEach(this::addEventDispatcherHandler);
		} catch (Throwable e) {
			getLogger().error("extension error",e);
		}

	}

	private void addEventDispatcherHandler(Class<? extends EventDispatcherHandler> clazz) {
		try {
			EventDispatcherHandler h = injectObjectField(clazz.newInstance());
			addEventHandler(h.eventType(), h);
		} catch (InstantiationException|IllegalAccessException e) {
			throw new Error(e);
		}
	}

	@Override
	public void destroy(){
		super.destroy();
		modules.forEach(ISFSModule::destroy);
		GoldLog.info("GameServerExtension destroy");
	}

}
