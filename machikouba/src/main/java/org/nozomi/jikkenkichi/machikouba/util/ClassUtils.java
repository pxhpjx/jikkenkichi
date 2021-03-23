package org.nozomi.jikkenkichi.machikouba.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

public class ClassUtils {

    /**
     * get a field value from @Annotation
     *
     * @param annotation
     * @param property
     * @return
     * @throws Exception
     */
    public static Object getAnnotationValue(Annotation annotation, String property) throws NoSuchFieldException, IllegalAccessException {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        Map map = (Map) getFieldValue(invocationHandler, "memberValues");
        return map != null ? map.get(property) : null;
    }

    /**
     * force get a field value
     *
     * @param object
     * @param property
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Object getFieldValue(T object, String property) throws NoSuchFieldException, IllegalAccessException {
        Class<T> currClass = (Class<T>) object.getClass();
        Field field = currClass.getDeclaredField(property);
        field.setAccessible(true);
        return field.get(object);
    }

}
