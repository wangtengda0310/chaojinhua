package com.igame.core.di;

import com.igame.core.ISFSModule;
import com.igame.core.data.ClassXmlDataLoader;
import org.slf4j.Logger;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class JarApplicationContext {
    public Set<ISFSModule> modules = new HashSet<>();

    public HashSet<Class> classesInJarFile = new HashSet<>();
    public Map<Class,Object> cachedObjects = new HashMap<>();
    public Map<Class,Set<Class>> classOfInterface = new HashMap<>();

    public <T> T injectObjectField(T needInjectField) {

        try {
            doReflact(needInjectField);

            return needInjectField;
        } catch (InstantiationException | IllegalAccessException e) {
            getLogger().error("initPlayerTop {} error", needInjectField, e);
            throw new Error(e);
        }
    }

    private <T> T injectHandlerClassField(Class<T> clazz) {

        try {
            T handler = (T) cachedObjects.get(clazz);

            doReflact(handler);

            return handler;
        } catch (InstantiationException | IllegalAccessException e) {
            getLogger().error("initPlayerTop {} error", clazz.getSimpleName(), e);
            throw new Error(e);
        }

    }

    ClassXmlDataLoader loader = new ClassXmlDataLoader("resource/");

    private <T> void doReflact(T component) throws IllegalAccessException, InstantiationException {
        Class<?> aClass = component.getClass();
        do {

            for (Field field : aClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.get(component) != null) {
                    continue;
                }
                Class<?> fieldClass = field.getType();
                if (field.isAnnotationPresent(LoadXml.class)) {    // todo observer?
                    Object data = loader.loadData(fieldClass, field.getAnnotation(LoadXml.class).value());
                    field.set(component, data);
                }
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
    Logger logger;

    public Logger getLogger() {
        return logger;
    }

    public JarApplicationContext(Logger logger) {
        this.logger = logger;
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
            getLogger().error("",e);
        }
    }

}
