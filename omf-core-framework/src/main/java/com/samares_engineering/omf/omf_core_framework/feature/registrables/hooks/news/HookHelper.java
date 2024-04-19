package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.news;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.news.annotation.OnProjectClosed;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.news.annotation.OnProjectOpened;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.news.annotation.ProjectHook;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HookHelper {

    public static List<Method> getAllProjectHooks(Class<?> clazz) {
        return getMethodsWithMetaAnnotation(clazz, ProjectHook.class);
    }

    public static List<Method> getAllOnProjectOpenedHooks(Class<?> clazz) {
        return getMethodsWithAnnotation(clazz, OnProjectOpened.class);
    }

    public static List<Method> getAllOnProjectClosedHooks(Class<?> clazz) {
        return getMethodsWithAnnotation(clazz, OnProjectClosed.class);
    }

    public static boolean isHookItem(Object object) {
        return !getMethodsWithMetaAnnotation(object.getClass(), ProjectHook.class).isEmpty();
    }


    public static List<Method> getMethodsWithMetaAnnotation(Class<?> clazz, Class<? extends Annotation> metaAnnotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> Arrays.stream(method.getAnnotations())
                        .anyMatch(annotation -> annotation.annotationType().isAnnotationPresent(metaAnnotation)))
                .collect(Collectors.toList());
    }


    public static List<Method> getMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotation))
                .collect(Collectors.toList());

    }

    public static void invokeMethodsWithAnnotation(Object object, Class<? extends Annotation> annotation) {
        getMethodsWithAnnotation(object.getClass(), annotation).forEach(method -> {
            try {
                method.invoke(object);
            } catch (Exception e) {
//                ErrorHandler2.getInstance().handleException(e);
            }
        });
    }
}
