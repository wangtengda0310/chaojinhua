package com.igame.core.di;

import com.igame.core.ISFSModule;
import com.igame.core.data.ClassXmlDataLoader;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
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

    private void listFilesRecursively(List<String> foundFiles, File currentFileOrDir) {

        if (currentFileOrDir == null || !currentFileOrDir.exists()) {
            return;
        }
        File[] files = currentFileOrDir.listFiles();
        if (files == null) {
            foundFiles.add(currentFileOrDir.getAbsolutePath());
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                listFilesRecursively(foundFiles, file);
            } else {
                String substring = getClass().getResource("/").getFile().substring(1);
                foundFiles.add(file.getAbsolutePath().substring(substring.length()));
            }
        }
    }

    public JarApplicationContext(Logger logger,String rootDir) {
        this.logger = logger;
        try {
            Stream<String> classNames;
            File rootFile = new File(rootDir);
            if (!rootFile.exists()) {
                return;
            }
            if(rootFile.isFile()) {
                String fileLoacation = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
                classNames = new JarFile(fileLoacation)
                        .stream()
                        .map(JarEntry::getName);
            } else {
                List<String> foundFiles = new LinkedList<>();
                listFilesRecursively(foundFiles, rootFile);
                classNames = foundFiles.stream();
            }
            classNames
                    .peek(System.out::println)
                    .filter(name -> name.endsWith(".class"))
                    .filter(name -> !name.contains("$"))
                    .map(name -> name.substring(0,name.indexOf(".class")))
                    .map(name -> name.replace("\\","."))
                    .map(name -> name.replace("/","."))
                    .map(name-> {
                        try {
                            return Class.forName(name);
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

    public void addAnnotationHandler(Class<? extends Annotation> annotationClass, AnnotationReflectionHandler handler) {
        getLogger().info(annotationClass+","+handler);
        cachedObjects.entrySet().stream()
                .flatMap(e->{
                    return Arrays.stream(e.getKey().getDeclaredFields())
                            .filter(f->f.isAnnotationPresent(annotationClass))
                            .map(f-> {
                                Annotation annotation = f.getAnnotation(annotationClass);
                                try {
                                    f.setAccessible(true);
                                    return new Object[]{f.get(e.getValue()), annotation};
                                } catch (IllegalAccessException ignore) {
                                    return null;
                                }
                            })
                            .filter(Objects::nonNull)
                            .filter(f->((Object[])f)[0] instanceof Map);

                })
                .peek(f->getLogger().debug("auto remove key on player offline: {} {} {}", f[0].getClass(), f[0], f[1]))
                .forEach(f->handler.handle((Map)f[0], (Annotation)f[1]));
    }
}
